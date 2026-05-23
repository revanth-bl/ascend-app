package com.example.viewmodel

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repo = Repository(db)

    // Expose DB Flows
    val userProfile: StateFlow<UserProfileEntity?> = repo.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allMissions: StateFlow<List<MissionEntity>> = repo.allMissions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBosses: StateFlow<List<BossEntity>> = repo.allBosses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSkills: StateFlow<List<SkillEntity>> = repo.allSkills
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCosmetics: StateFlow<List<CosmeticEntity>> = repo.allCosmetics
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chatLogs: StateFlow<List<ChatLogEntity>> = repo.chatLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI States
    val coachTone = mutableStateOf("Tactical") // Harsh, Tactical, Motivational, Calm, Military, Anime
    val coachInput = mutableStateOf("")
    val isCoachGenerating = mutableStateOf(false)

    // Focus Timer State
    private var timerJob: Job? = null
    val isFocusActive = mutableStateOf(false)
    val focusTargetMinutes = mutableStateOf(25)
    val focusTimeElapsedSeconds = mutableStateOf(0)
    val isFocusCompleted = mutableStateOf(false)

    // Merchant loot state
    val lastItemLooted = mutableStateOf<CosmeticEntity?>(null)
    val showLootDialog = mutableStateOf(false)

    init {
        // Run seeding in background coroutine on launch!
        viewModelScope.launch {
            repo.seedDatabaseIfNeeded()
        }
    }

    // --- Core Operations ---

    fun addNewMission(title: String, category: String, difficulty: String, durationMinutes: Int, isSide: Boolean = false, isSpecial: Boolean = false) {
        viewModelScope.launch {
            val rarityOptions = listOf("Common", "Rare", "Epic", "Legendary")
            val rarity = if (isSpecial) "Mythic" else if (isSide) "Epic" else if (difficulty == "Legendary") "Legendary" else rarityOptions.random()
            
            val xpBasis = when(difficulty) {
                "Easy" -> 15
                "Medium" -> 25
                "Hard" -> 45
                "Legendary" -> 75
                else -> 20
            }
            val rrBasis = when(difficulty) {
                "Easy" -> 5
                "Medium" -> 12
                "Hard" -> 22
                "Legendary" -> 45
                else -> 10
            }

            val newM = MissionEntity(
                title = title,
                category = category,
                rarity = rarity,
                difficulty = difficulty,
                durationMinutes = durationMinutes,
                xpReward = xpBasis,
                rrReward = rrBasis,
                failurePenalty = if (isSide || isSpecial) 0 else (rrBasis * 0.8).toInt(),
                isSideQuest = isSide,
                isSpecialQuest = isSpecial,
                isDailyTask = !isSide && !isSpecial
            )
            repo.insertMission(newM)
        }
    }

    // Spawn Random "Tactical Side Quest"
    fun scanIntelSideQuest() {
        viewModelScope.launch {
            val randomQuests = listOf(
                Pair("Touch Grass Tactical Audit", "Go outside, observe surroundings for 15 minutes."),
                Pair("Neural Shield Hydration Cycle", "Consume 500ml of pure electrolyte H2O."),
                Pair("Tactical Core Breath Sequence", "Operate 4-7-8 breathing sequence for 5 cycles."),
                Pair("Digital Detox Comply Run", "Stow screen device away for 30 minutes straight."),
                Pair("Discipline Shower Therapy", "Initiate 3 minutes sensory cold shower run."),
                Pair("Knowledge Codex Absorb Unit", "Scan 10 pages of high density intellectual codex.")
            )
            val index = (0 until randomQuests.size).random()
            val questData = randomQuests[index]

            // Mark as side quest (no failure penalty on omissions)
            val sideQ = MissionEntity(
                title = questData.first,
                category = "Discipline",
                rarity = "Epic",
                difficulty = "Easy",
                durationMinutes = 15,
                xpReward = 35,
                rrReward = 15,
                failurePenalty = 0,
                isSideQuest = true,
                isDailyTask = false
            )
            repo.insertMission(sideQ)

            // Feed Coach notification
            repo.insertCoachMessage("INTEL SENSOR ALERT: Located high priority Tactical Side Quest: '${sideQ.title}'. Complete in short window for bonus reward rating.")
        }
    }

    // --- Daily Routine Subsystem Controllers ---

    fun completeDailyRoutineTask(taskType: String) {
        viewModelScope.launch {
            repo.completeDailyRoutineTask(taskType)
        }
    }

    fun failDailyRoutineTask(taskType: String) {
        viewModelScope.launch {
            repo.failDailyRoutineTask(taskType)
        }
    }

    fun closeoutDailyCycle() {
        viewModelScope.launch {
            repo.closeoutDailyCycle()
        }
    }

    fun completeMission(missionId: Int) {
        viewModelScope.launch {
            repo.completeMission(missionId)
        }
    }

    fun failMission(missionId: Int) {
        viewModelScope.launch {
            repo.failMission(missionId)
        }
    }

    fun clearCompletedMissions() {
        viewModelScope.launch {
            repo.cleanCompletedMissions()
        }
    }

    // --- User Identity and Settings Controllers ---

    fun registerUser(email: String, password: String, onFinished: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            if (email.isEmpty() || password.isEmpty()) {
                onFinished(false, "Values cannot be blank")
                return@launch
            }
            val success = repo.registerUser(email, password)
            if (success) {
                onFinished(true, "Operative registered successfully!")
            } else {
                onFinished(false, "Identity already registered in database!")
            }
        }
    }

    fun loginUser(email: String, password: String, rememberDevice: Boolean, onFinished: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            if (email.isEmpty() || password.isEmpty()) {
                onFinished(false, "Values cannot be blank")
                return@launch
            }
            val success = repo.loginUser(email, password, rememberDevice)
            if (success) {
                onFinished(true, "Operative identity verified!")
            } else {
                onFinished(false, "Invalid credentials or missing account!")
            }
        }
    }

    fun logoutUser() {
        viewModelScope.launch {
            repo.logoutUser()
        }
    }

    fun updateActiveProfileSettings(
        username: String,
        bio: String,
        customAvatarUri: String,
        bannerImage: String,
        appTheme: String,
        soundEffectsEnabled: Boolean,
        animationIntensity: String,
        notificationsEnabled: Boolean
    ) {
        viewModelScope.launch {
            repo.updateActiveProfileSettings(
                username = username,
                bio = bio,
                customAvatarUri = customAvatarUri,
                bannerImage = bannerImage,
                appTheme = appTheme,
                soundEffectsEnabled = soundEffectsEnabled,
                animationIntensity = animationIntensity,
                notificationsEnabled = notificationsEnabled
            )
        }
    }

    fun resetProgress() {
        viewModelScope.launch {
            repo.resetProgress()
        }
    }

    fun backupData() {
        viewModelScope.launch {
            repo.backupData()
        }
    }

    fun exportDataToText(onCompleted: (String) -> Unit) {
        viewModelScope.launch {
            val content = repo.exportDataToText()
            onCompleted(content)
        }
    }

    // --- Focus Timer Controller ---

    fun startFocusSession(minutes: Int) {
        isFocusActive.value = true
        focusTargetMinutes.value = minutes
        focusTimeElapsedSeconds.value = 0
        isFocusCompleted.value = false

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            val targetSecs = minutes * 60
            while (focusTimeElapsedSeconds.value < targetSecs && isFocusActive.value) {
                delay(1000)
                focusTimeElapsedSeconds.value += 1
            }
            if (focusTimeElapsedSeconds.value >= targetSecs) {
                // Succeeded!
                isFocusCompleted.value = true
                isFocusActive.value = false
                repo.handleFocusSession(minutes)
            }
        }
    }

    fun stopOrAbandonFocusSession() {
        if (isFocusActive.value) {
            val elapsedMinutes = focusTimeElapsedSeconds.value / 60
            isFocusActive.value = false
            timerJob?.cancel()

            // Partial reward or penality depending on elapsed metrics
            viewModelScope.launch {
                if (elapsedMinutes >= 5) {
                    repo.handleFocusSession(elapsedMinutes)
                    repo.insertCoachMessage("TACTICAL BREACH COMPLETED: Focus session aborted early. Logged partial data of $elapsedMinutes focus run.")
                } else {
                    repo.insertCoachMessage("FOCUS RUN ABANDONED: Operative aborted session prior to minimal 5-minute telemetry. Zero rating awarded.")
                }
            }
        }
    }

    // --- AI Coach Chat Subsystem ---

    fun sendPlayerChatMessage() {
        val query = coachInput.value.trim()
        if (query.isEmpty()) return

        coachInput.value = ""
        isCoachGenerating.value = true

        viewModelScope.launch {
            // Save query in chat logs first
            repo.insertChatMessage(query)

            // Get profile and recent context
            val profile = userProfile.value ?: UserProfileEntity()
            val history = chatLogs.value

            // Generate using Gemini REST Client in safe dispatch thread
            val coachReply = withContext(Dispatchers.IO) {
                GeminiClient.generateCoachFeedback(query, history, profile, coachTone.value)
            }

            // Save Response
            repo.insertCoachMessage(coachReply)
            isCoachGenerating.value = false
        }
    }

    fun setTacticalCoachTone(tone: String) {
        coachTone.value = tone
        viewModelScope.launch {
            repo.insertCoachMessage("COACH PROFILE RECONFIGURED: Telemetry voice dynamic recalibrated to '$tone'. Stand by.")
        }
    }

    // --- RPG Tree & Customization actions ---

    fun selectClass(className: String) {
        viewModelScope.launch {
            repo.chooseClass(className)
        }
    }

    fun buyCosmetic(cosmeticId: String) {
        viewModelScope.launch {
            repo.purchaseCosmetic(cosmeticId)
        }
    }

    fun equipCosmetic(cosmeticId: String, category: String) {
        viewModelScope.launch {
            repo.equipCosmetic(cosmeticId, category)
        }
    }

    fun unlockSkillPerk(skillId: String) {
        viewModelScope.launch {
            repo.activeSkill(skillId)
        }
    }

    fun purchaseLootCrate() {
        viewModelScope.launch {
            val item = repo.rollLootCrate()
            if (item != null) {
                lastItemLooted.value = item
                showLootDialog.value = true
            } else {
                lastItemLooted.value = null
                showLootDialog.value = true
            }
        }
    }

    fun addNewBoss(name: String, description: String, health: Float, rewardTitle: String, rewardItem: String) {
        viewModelScope.launch {
            val newBoss = BossEntity(
                name = name,
                description = description,
                currentHealth = health,
                maxHealth = health,
                rewardTitle = rewardTitle,
                rewardItem = rewardItem,
                xpReward = 300,
                goldReward = 80
            )
            db.bossDao().insertBoss(newBoss)
            repo.insertCoachMessage("COOP CAMPAIGN INITIATED: A new legendary threat '${newBoss.name}' has emerged in real world coordinates. Mobilize operations.")
        }
    }

    fun deleteBoss(boss: BossEntity) {
        viewModelScope.launch {
            db.bossDao().deleteBoss(boss)
        }
    }

    fun deleteMission(mission: MissionEntity) {
        viewModelScope.launch {
            db.missionDao().deleteMission(mission)
        }
    }
}
