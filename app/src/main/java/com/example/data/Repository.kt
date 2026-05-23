package com.example.data

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository(private val db: AppDatabase) {

    val userProfile: Flow<UserProfileEntity?> = db.userProfileDao().getUserProfile()
    val allMissions: Flow<List<MissionEntity>> = db.missionDao().getAllMissions()
    val allBosses: Flow<List<BossEntity>> = db.bossDao().getAllBosses()
    val allSkills: Flow<List<SkillEntity>> = db.skillDao().getAllSkills()
    val allCosmetics: Flow<List<CosmeticEntity>> = db.cosmeticDao().getAllCosmetics()
    val chatLogs: Flow<List<ChatLogEntity>> = db.chatLogDao().getChatLogs()

    suspend fun seedDatabaseIfNeeded() = withContext(Dispatchers.IO) {
        val currentProfile = db.userProfileDao().getUserProfileSync()
        if (currentProfile == null) {
            Log.d("Repository", "Seeding database with default values...")
            
            // Seed Profile
            db.userProfileDao().insertUserProfile(UserProfileEntity())

            // Seed Skills
            val defaultSkills = listOf(
                SkillEntity("discipline_decay", "Decay Shield", "Discipline", "Reduces RR decay from inactivity by 50%", "Decay rate reduced", false, 1),
                SkillEntity("xp_booster", "XP Hyperdrive", "Focus", "Gain 20% additional Experience from completed operations", "+20% Operations XP", false, 1),
                SkillEntity("streak_shield", "Streak Protection", "Spirit", "Preserves your streak once if you miss daily objectives", "One-time streak shield", false, 2),
                SkillEntity("burnout_resist", "Core Coolant", "Spirit", "Active Burnout accumulates 30% slower during deep work", "-30% Burnout Rate", false, 1),
                SkillEntity("crit_focus", "Overtime Drive", "Focus", "Adds 25% focus rewards on focus runs longer than 45 minutes", "+25% Overtime Focus XP", false, 2),
                SkillEntity("mission_reroll", "Contract Override", "Intellect", "Unlock the ability to reroll active tactical contracts", "Rerolling enabled", false, 1),
                SkillEntity("gym_berserker", "Berserker Will", "Physical", "Completing gym missions heals health decay and drops boss defense", "+15% damage to Bosses", false, 2)
            )
            db.skillDao().insertSkills(defaultSkills)

            // Seed Cosmetics
            val defaultCosmetics = listOf(
                CosmeticEntity("outfit_basic", "Challenger Suit", "outfit", "Common", 0, true, true, "Standard tactical suit for recruits."),
                CosmeticEntity("outfit_marine", "Neon Stealth Shell", "outfit", "Epic", 150, false, false, "Synthetic carbon shell that pulses in cyan."),
                CosmeticEntity("outfit_apex", "Apex Radiant Plate", "outfit", "Mythic", 450, false, false, "Legendary cybernetic armor of the elites."),
                CosmeticEntity("weapon_pen", "Stylus Dagger", "weapon", "Common", 0, true, true, "Scribbles goals and executes targets."),
                CosmeticEntity("weapon_katana", "Nippon Code Katana", "weapon", "Epic", 250, false, false, "Forged in compiled steel. Slashes syntax bugs."),
                CosmeticEntity("weapon_sabre", "Void Plasma Saber", "weapon", "Legendary", 500, false, false, "Radiates cosmic plasma energy of focused will."),
                CosmeticEntity("pet_drone", "Tactical Scan Bot", "pet", "Rare", 120, false, false, "Autonomous drone measuring real-time focus vectors."),
                CosmeticEntity("pet_dragon", "DeepMind Wyvern", "pet", "Mythic", 600, false, false, "Digital holographic companion mimicking core consistency."),
                CosmeticEntity("title_recruit", "Vanguard Recruit", "title", "Common", 0, true, true, "Entered the ranks of ascendancy."),
                CosmeticEntity("title_slayer", "Boss Eradicator", "title", "Epic", 100, false, false, "Showed true resistance against real life entropy."),
                CosmeticEntity("title_god", "Sovereign of Focus", "title", "Mythic", 400, false, false, "Mastered absolute concentration.")
            )
            db.cosmeticDao().insertCosmetics(defaultCosmetics)

            // Seed Missions
            val defaultMissions = listOf(
                MissionEntity(title = "Execute Gym Weight Matrix", category = "Fitness", rarity = "Epic", difficulty = "Hard", durationMinutes = 60, xpReward = 50, rrReward = 20, failurePenalty = 12),
                MissionEntity(title = "Synchronize Daily Code Files", category = "Coding", rarity = "Common", difficulty = "Medium", durationMinutes = 45, xpReward = 25, rrReward = 12, failurePenalty = 8),
                MissionEntity(title = "Calibrate Alpha Business Strategy", category = "Business", rarity = "Legendary", difficulty = "Legendary", durationMinutes = 90, xpReward = 80, rrReward = 35, failurePenalty = 20),
                MissionEntity(title = "Engage Core Mind Calming", category = "Mental Health", rarity = "Rare", difficulty = "Easy", durationMinutes = 15, xpReward = 15, rrReward = 8, failurePenalty = 5),
                MissionEntity(title = "Deep Focus Isolation Protocol", category = "Deep Focus", rarity = "Epic", difficulty = "Hard", durationMinutes = 50, xpReward = 45, rrReward = 18, failurePenalty = 10)
            )
            for (m in defaultMissions) {
                db.missionDao().insertMission(m)
            }

            // Seed Bosses
            val defaultBosses = listOf(
                BossEntity(name = "The Entropy Semester", description = "CS Final Exams & Academic Projects.", currentHealth = 100f, maxHealth = 100f, rewardTitle = "Lord Scholar", rewardItem = "Excalibur Stylus"),
                BossEntity(name = "Colossus Gym Campaign", description = "Achieve 90 days total body transformation.", currentHealth = 180f, maxHealth = 180f, rewardTitle = "Unstoppable Berserker", rewardItem = "Gym Dumbbells V2")
            )
            for (b in defaultBosses) {
                db.bossDao().insertBoss(b)
            }

            // Seed First Chat message from AI Coach
            db.chatLogDao().insertChatLog(ChatLogEntity(
                sender = "COACH",
                message = "TACTICAL BRIEFING: Welcome to ASCEND, Operative. I am your elite tactical analyst. Inconsistency is your enemy. Keep your rank rating up. Do not lose your focus."
            ))
        }
    }

    // --- Action Methods ---

    suspend fun insertMission(mission: MissionEntity) = withContext(Dispatchers.IO) {
        db.missionDao().insertMission(mission)
    }

    suspend fun completeMission(missionId: Int) = withContext(Dispatchers.IO) {
        val mission = db.missionDao().getMissionById(missionId) ?: return@withContext
        if (mission.isCompleted) return@withContext

        // Mark completed
        val completedMission = mission.copy(isCompleted = true)
        db.missionDao().updateMission(completedMission)

        // Award rewards
        val profile = db.userProfileDao().getUserProfileSync() ?: return@withContext
        
        // Boost modifiers from Skill Tree
        val skills = db.skillDao().getAllSkills().firstOrNull() ?: emptyList()
        val hasXpBooster = skills.find { it.id == "xp_booster" && it.isUnlocked } != null
        val xpMultiplier = if (hasXpBooster) 1.20f else 1.0f

        val hasBerserker = skills.find { it.id == "gym_berserker" && it.isUnlocked } != null
        
        // Refined RR Quest System calculations
        val earnedRr = if (mission.isSpecialQuest) {
            when (mission.difficulty) {
                "Easy" -> 100
                "Medium" -> 200
                "Hard" -> 350
                "Legendary" -> 500
                else -> 200
            }
        } else if (mission.isSideQuest) {
            when (mission.difficulty) {
                "Easy" -> 20
                "Medium" -> 30
                "Hard" -> 40
                "Legendary" -> 50
                else -> 25
            }
        } else {
            mission.rrReward
        }

        // Higher XP for quests as well
        val earnedXpBase = if (mission.isSpecialQuest) {
            when (mission.difficulty) {
                "Easy" -> 150
                "Medium" -> 250
                "Hard" -> 400
                "Legendary" -> 600
                else -> 250
            }
        } else if (mission.isSideQuest) {
            mission.xpReward + 15
        } else {
            mission.xpReward
        }

        val earnedXp = (earnedXpBase * xpMultiplier).toInt()

        val earnedCoins = if (mission.isSpecialQuest) {
            120
        } else if (mission.isSideQuest) {
            40
        } else {
            when (mission.rarity) {
                "Common" -> 10
                "Rare" -> 20
                "Epic" -> 35
                "Legendary" -> 60
                "Mythic" -> 100
                else -> 10
            }
        }

        // Deal damage to any active boss if the mission matches
        val bosses = db.bossDao().getAllBosses().firstOrNull() ?: emptyList()
        val activeBoss = bosses.find { !it.isCompleted }
        if (activeBoss != null) {
            val damageBase = when (mission.difficulty) {
                "Easy" -> 5f
                "Medium" -> 12f
                "Hard" -> 25f
                "Legendary" -> 50f
                else -> 10f
            }
            val damageMultiplier = if (hasBerserker && mission.category == "Fitness") 1.25f else 1.0f
            // Special quest deals double damage to bosses
            val typeMultiplier = if (mission.isSpecialQuest) 2.0f else if (mission.isSideQuest) 1.2f else 1.0f
            val finalDamage = damageBase * damageMultiplier * typeMultiplier
            val newHealth = (activeBoss.currentHealth - finalDamage).coerceAtLeast(0f)
            if (newHealth <= 0f) {
                // Completed boss!
                db.bossDao().updateBoss(activeBoss.copy(currentHealth = 0f, isCompleted = true))
                // Award boss rewards!
                val unlockCosmetic = CosmeticEntity(
                    id = "unlocked_boss_${activeBoss.id}",
                    name = activeBoss.rewardItem,
                    category = "weapon",
                    rarity = "Legendary",
                    coinCost = 0,
                    isUnlocked = true,
                    isEquipped = false,
                    description = "Forged through blood and iron by slaying ${activeBoss.name}"
                )
                db.cosmeticDao().insertCosmetics(listOf(unlockCosmetic))
                
                // Award gold/XP to profile
                updateProfileWithRewards(
                    profile = profile, 
                    earnedXp = activeBoss.xpReward, 
                    earnRr = 50, 
                    earnCoins = activeBoss.goldReward,
                    customTitleReward = activeBoss.rewardTitle,
                    isSideQuest = mission.isSideQuest
                )
            } else {
                db.bossDao().updateBoss(activeBoss.copy(currentHealth = newHealth))
                // Standard reward update
                updateProfileWithRewards(profile, earnedXp, earnedRr, earnedCoins, isSideQuest = mission.isSideQuest)
            }
        } else {
            // Standard reward update
            updateProfileWithRewards(profile, earnedXp, earnedRr, earnedCoins, isSideQuest = mission.isSideQuest)
        }
    }

    private suspend fun updateProfileWithRewards(
        profile: UserProfileEntity,
        earnedXp: Int,
        earnRr: Int,
        earnCoins: Int,
        customTitleReward: String? = null,
        isSideQuest: Boolean = false
    ) {
        var currentXp = profile.xp + earnedXp
        var currentLevel = profile.level
        var xpNeeded = profile.xpToNextLevel
        var currentSkillPoints = profile.skillPoints

        // Level Up Loop
        while (currentXp >= xpNeeded) {
            currentXp -= xpNeeded
            currentLevel += 1
            xpNeeded = currentLevel * 100 + 100
            currentSkillPoints += 1 // award skill point on level up!
        }

        // Rank Update (Valorant Style)
        var currentRr = profile.rankRating + earnRr
        var currentRank = profile.rank
        var currentDivision = profile.division

        val rankOrder = listOf("Recruit", "Bronze", "Silver", "Gold", "Elite", "Apex", "Mythic")
        
        if (currentRr >= 100) {
            while (currentRr >= 100) {
                if (currentDivision < 3) {
                    currentDivision += 1
                    currentRr -= 100
                } else {
                    // rank promote!
                    val rankIndex = rankOrder.indexOf(currentRank)
                    if (rankIndex < rankOrder.lastIndex) {
                        currentRank = rankOrder[rankIndex + 1]
                        currentDivision = 1
                        currentRr -= 100
                        currentRr = currentRr.coerceAtLeast(10) // buffer reward for promotion!
                    } else {
                        // Maximum rank cap (Mythic)
                        currentRr = 100
                        break
                    }
                }
            }
        }

        // Side quests build more momentum and streak!
        val streakInc = if (isSideQuest) 2 else 1
        val updatedProfile = profile.copy(
            level = currentLevel,
            xp = currentXp,
            xpToNextLevel = xpNeeded,
            rankRating = currentRr,
            rank = currentRank,
            division = currentDivision,
            goldCoins = profile.goldCoins + earnCoins,
            skillPoints = currentSkillPoints,
            streak = profile.streak + streakInc,
            momentum = (profile.momentum + (if (isSideQuest) 0.15f else 0.08f)).coerceAtMost(1.0f),
            equippedTitle = customTitleReward ?: profile.equippedTitle,
            lastActiveTimestamp = System.currentTimeMillis()
        )

        db.userProfileDao().updateUserProfile(updatedProfile)
    }

    suspend fun failMission(missionId: Int) = withContext(Dispatchers.IO) {
        val mission = db.missionDao().getMissionById(missionId) ?: return@withContext
        if (mission.isCompleted) return@withContext

        // Delete mission from active board
        db.missionDao().deleteMission(mission)

        // Side missions and special quests have zero penalty if missed
        if (mission.isSideQuest || mission.isSpecialQuest) {
            db.chatLogDao().insertChatLog(ChatLogEntity(
                sender = "COACH",
                message = "TACTICAL BRIEFING: Optional quest logged off. Gained 0 RR | Penalty waived: '${mission.title}' is optional."
            ))
            return@withContext
        }

        // Standard missions penalize RR on failure/miss
        val profile = db.userProfileDao().getUserProfileSync() ?: return@withContext
        
        val skills = db.skillDao().getAllSkills().firstOrNull() ?: emptyList()
        val hasDecayShield = skills.find { it.id == "discipline_decay" && it.isUnlocked } != null
        val penaltyMultiplier = if (hasDecayShield) 0.5f else 1.0f
        
        val rrPenalty = (mission.failurePenalty * penaltyMultiplier).toInt()

        applyProfileRrPenalty(profile, rrPenalty, "Failed mandatory operation: '${mission.title}'")
    }

    // --- Daily Routine Control Subsystem ---

    suspend fun completeDailyRoutineTask(taskType: String) = withContext(Dispatchers.IO) {
        val profile = db.userProfileDao().getUserProfileSync() ?: return@withContext
        val currentStatus = when (taskType) {
            "pushups" -> profile.dailyPushUps
            "situps" -> profile.dailySitUps
            "squats" -> profile.dailySquats
            "run" -> profile.dailyRun
            else -> "COMPLETED"
        }
        if (currentStatus == "COMPLETED") return@withContext

        val updatedProfile = when (taskType) {
            "pushups" -> profile.copy(dailyPushUps = "COMPLETED")
            "situps" -> profile.copy(dailySitUps = "COMPLETED")
            "squats" -> profile.copy(dailySquats = "COMPLETED")
            "run" -> profile.copy(dailyRun = "COMPLETED")
            else -> profile
        }

        val rrGain = when (taskType) {
            "pushups" -> 10
            "situps" -> 10
            "squats" -> 10
            "run" -> 25
            else -> 0
        }
        val xpGain = when (taskType) {
            "pushups" -> 20
            "situps" -> 20
            "squats" -> 20
            "run" -> 50
            else -> 0
        }
        val coinGain = when (taskType) {
            "pushups" -> 15
            "situps" -> 15
            "squats" -> 15
            "run" -> 35
            else -> 0
        }

        updateProfileWithRewards(updatedProfile, xpGain, rrGain, coinGain)

        db.chatLogDao().insertChatLog(ChatLogEntity(
            sender = "COACH",
            message = "ROUTINE SECURED: Finished physical routine '${taskType.uppercase()}'. Awarded +$rrGain RR!"
        ))
    }

    suspend fun failDailyRoutineTask(taskType: String) = withContext(Dispatchers.IO) {
        val profile = db.userProfileDao().getUserProfileSync() ?: return@withContext
        val currentStatus = when (taskType) {
            "pushups" -> profile.dailyPushUps
            "situps" -> profile.dailySitUps
            "squats" -> profile.dailySquats
            "run" -> profile.dailyRun
            else -> "FAILED"
        }
        if (currentStatus == "FAILED" || currentStatus == "COMPLETED") return@withContext

        val updatedProfile = when (taskType) {
            "pushups" -> profile.copy(dailyPushUps = "FAILED")
            "situps" -> profile.copy(dailySitUps = "FAILED")
            "squats" -> profile.copy(dailySquats = "FAILED")
            "run" -> profile.copy(dailyRun = "FAILED")
            else -> profile
        }

        val rrPenalty = when (taskType) {
            "pushups" -> 15
            "situps" -> 15
            "squats" -> 15
            "run" -> 30
            else -> 0
        }

        applyProfileRrPenalty(updatedProfile, rrPenalty, "Routine omitted manually: '${taskType.uppercase()}'")
    }

    suspend fun closeoutDailyCycle() = withContext(Dispatchers.IO) {
        val profile = db.userProfileDao().getUserProfileSync() ?: return@withContext
        
        var penaltyAccum = 0
        var completedCount = 0
        
        if (profile.dailyPushUps == "PENDING") penaltyAccum += 15 else if (profile.dailyPushUps == "COMPLETED") completedCount++
        if (profile.dailySitUps == "PENDING") penaltyAccum += 15 else if (profile.dailySitUps == "COMPLETED") completedCount++
        if (profile.dailySquats == "PENDING") penaltyAccum += 15 else if (profile.dailySquats == "COMPLETED") completedCount++
        if (profile.dailyRun == "PENDING") penaltyAccum += 30 else if (profile.dailyRun == "COMPLETED") completedCount++

        val skills = db.skillDao().getAllSkills().firstOrNull() ?: emptyList()
        val hasDecayShield = skills.find { it.id == "discipline_decay" && it.isUnlocked } != null
        val penaltyMultiplier = if (hasDecayShield) 0.5f else 1.0f

        val finalPenalty = (penaltyAccum * penaltyMultiplier).toInt()

        var currentRr = profile.rankRating
        var currentStreak = profile.dailyRoutineStreakCount
        var totalCoinsEarned = 0
        var totalXpEarned = 0

        val feedbackMsg: String
        
        if (completedCount == 4) {
            // Full routine completed!
            currentStreak += 1
            val streakBonus = (currentStreak * 5).coerceAtMost(50)
            val totalBonusRr = 40 + streakBonus
            currentRr += totalBonusRr
            totalCoinsEarned += 50
            totalXpEarned += 100
            feedbackMsg = "ROUTINE MATRIX COMPLETE: Maximum completion parameters secured! Full bonus awarded (+40 RR) plus consecutive streak booster (+$streakBonus RR, Streak: $currentStreak days)!"
        } else if (completedCount == 0) {
            // Miss full daily routine = heavy decay penalty (-50 RR)
            currentStreak = 0
            val decayPenalty = (50 * penaltyMultiplier).toInt()
            currentRr -= decayPenalty
            feedbackMsg = "CRITICAL COGNITIVE DISSONANCE: Skipping entire physical matrix has triggered heavy discipline decay (-$decayPenalty RR)! Stand down."
        } else {
            // Partial completions
            currentStreak = 0
            currentRr -= finalPenalty
            feedbackMsg = "CYCLE TRANSITION LOGGED: Routine completed partially ($completedCount/4). Omission penalties have docked -$finalPenalty RR total."
        }

        // Apply Rank Up / Down rules manually for the final outcome
        var currentRank = profile.rank
        var currentDivision = profile.division
        val rankOrder = listOf("Recruit", "Bronze", "Silver", "Gold", "Elite", "Apex", "Mythic")

        // Handle negative RR resulting in demotions
        if (currentRr < 0) {
            while (currentRr < 0) {
                if (currentDivision > 1) {
                    currentDivision -= 1
                    currentRr = 100 + currentRr
                } else {
                    val rankIndex = rankOrder.indexOf(currentRank)
                    if (rankIndex > 0) {
                        currentRank = rankOrder[rankIndex - 1]
                        currentDivision = 3
                        currentRr = 75 // demotion shield buffer
                        break
                    } else {
                        currentRr = 0
                        break
                    }
                }
            }
        } else if (currentRr >= 100) {
            // Handle positive RR resulting in promotions
            while (currentRr >= 100) {
                if (currentDivision < 3) {
                    currentDivision += 1
                    currentRr -= 100
                } else {
                    val rankIndex = rankOrder.indexOf(currentRank)
                    if (rankIndex < rankOrder.lastIndex) {
                        currentRank = rankOrder[rankIndex + 1]
                        currentDivision = 1
                        currentRr -= 100
                        currentRr = currentRr.coerceAtLeast(10) // promotion buffer
                    } else {
                        currentRr = 100 // Cap
                        break
                    }
                }
            }
        }

        // Handle XP level ups
        var currentXp = profile.xp + totalXpEarned
        var currentLevel = profile.level
        var xpNeeded = profile.xpToNextLevel
        var currentSkillPoints = profile.skillPoints
        while (currentXp >= xpNeeded) {
            currentXp -= xpNeeded
            currentLevel += 1
            xpNeeded = currentLevel * 100 + 100
            currentSkillPoints += 1
        }

        val updatedProfile = profile.copy(
            rankRating = currentRr,
            rank = currentRank,
            division = currentDivision,
            dailyRoutineStreakCount = currentStreak,
            dailyPushUps = "PENDING",
            dailySitUps = "PENDING",
            dailySquats = "PENDING",
            dailyRun = "PENDING",
            level = currentLevel,
            xp = currentXp,
            xpToNextLevel = xpNeeded,
            goldCoins = profile.goldCoins + totalCoinsEarned,
            skillPoints = currentSkillPoints
        )

        db.userProfileDao().updateUserProfile(updatedProfile)

        db.chatLogDao().insertChatLog(ChatLogEntity(
            sender = "COACH",
            message = feedbackMsg
        ))
    }

    private suspend fun applyProfileRrPenalty(profile: UserProfileEntity, rrPenalty: Int, reason: String) {
        var currentRr = profile.rankRating - rrPenalty
        var currentRank = profile.rank
        var currentDivision = profile.division

        val rankOrder = listOf("Recruit", "Bronze", "Silver", "Gold", "Elite", "Apex", "Mythic")

        if (currentRr < 0) {
            if (currentDivision > 1) {
                currentDivision -= 1
                currentRr = 100 + currentRr
            } else {
                val rankIndex = rankOrder.indexOf(currentRank)
                if (rankIndex > 0) {
                    currentRank = rankOrder[rankIndex - 1]
                    currentDivision = 3
                    currentRr = 75 // demotion shield
                } else {
                    currentRr = 0
                }
            }
        }

        val updated = profile.copy(
            rankRating = currentRr,
            rank = currentRank,
            division = currentDivision,
            streak = 0, // Reset standard streak
            momentum = (profile.momentum - 0.15f).coerceAtLeast(0.0f)
        )
        db.userProfileDao().updateUserProfile(updated)

        db.chatLogDao().insertChatLog(ChatLogEntity(
            sender = "COACH",
            message = "PENALTY INFLICTED: $reason. Loss of $rrPenalty RR. Re-climbing is required."
        ))
    }

    suspend fun handleFocusSession(durationMinutes: Int) = withContext(Dispatchers.IO) {
        val profile = db.userProfileDao().getUserProfileSync() ?: return@withContext
        
        val skills = db.skillDao().getAllSkills().firstOrNull() ?: emptyList()
        val hasCritFocus = skills.find { it.id == "crit_focus" && it.isUnlocked } != null
        val multiplier = if (hasCritFocus && durationMinutes >= 45) 1.25f else 1.0f

        val xpGained = (durationMinutes * 2 * multiplier).toInt()
        val rrGained = (durationMinutes / 10).coerceAtLeast(1)
        val coinsGained = durationMinutes / 5

        updateProfileWithRewards(profile, xpGained, rrGained, coinsGained)

        // Increment stats
        val updatedProfile = db.userProfileDao().getUserProfileSync()?.copy(
            totalFocusMinutes = profile.totalFocusMinutes + durationMinutes,
            burnout = (profile.burnout - 0.1f).coerceAtLeast(0.0f) // focus meditation/reading reduces burnout!
        )
        if (updatedProfile != null) {
            db.userProfileDao().updateUserProfile(updatedProfile)
        }

        db.chatLogDao().insertChatLog(ChatLogEntity(
            sender = "COACH",
            message = "GRID SECURED: Finished $durationMinutes minutes ranked focus run. Gained $xpGained XP and $rrGained RR. Burnout levels suppressed."
        ))
    }

    suspend fun purchaseCosmetic(cosmeticId: String) = withContext(Dispatchers.IO) {
        val profile = db.userProfileDao().getUserProfileSync() ?: return@withContext
        val allC = db.cosmeticDao().getAllCosmetics().firstOrNull() ?: emptyList()
        val item = allC.find { it.id == cosmeticId } ?: return@withContext

        if (item.isUnlocked) return@withContext
        if (profile.goldCoins < item.coinCost) return@withContext

        // unlock it!
        db.cosmeticDao().updateCosmetic(item.copy(isUnlocked = true))

        // deduct coins
        db.userProfileDao().updateUserProfile(profile.copy(
            goldCoins = profile.goldCoins - item.coinCost
        ))
    }

    suspend fun equipCosmetic(cosmeticId: String, category: String) = withContext(Dispatchers.IO) {
        val allC = db.cosmeticDao().getAllCosmetics().firstOrNull() ?: emptyList()
        val item = allC.find { it.id == cosmeticId } ?: return@withContext
        if (!item.isUnlocked) return@withContext

        db.cosmeticDao().equipCosmetic(cosmeticId, category)

        // Sync directly user profile for equipped appearance
        val profile = db.userProfileDao().getUserProfileSync() ?: return@withContext
        val newProfile = when (category) {
            "outfit" -> profile.copy(equippedOutfit = item.name)
            "weapon" -> profile.copy(equippedWeapon = item.name)
            "pet" -> profile.copy(equippedPet = item.name)
            "background" -> profile.copy(equippedBackground = item.name)
            "title" -> profile.copy(equippedTitle = item.name)
            else -> profile
        }
        db.userProfileDao().updateUserProfile(newProfile)
    }

    suspend fun activeSkill(skillId: String) = withContext(Dispatchers.IO) {
        val profile = db.userProfileDao().getUserProfileSync() ?: return@withContext
        if (profile.skillPoints <= 0) return@withContext

        val skills = db.skillDao().getAllSkills().firstOrNull() ?: emptyList()
        val s = skills.find { it.id == skillId } ?: return@withContext
        if (s.isUnlocked) return@withContext

        // Unlock
        db.skillDao().updateSkill(s.copy(isUnlocked = true))

        // Deduct skill points
        db.userProfileDao().updateUserProfile(profile.copy(
            skillPoints = profile.skillPoints - 1
        ))

        db.chatLogDao().insertChatLog(ChatLogEntity(
            sender = "COACH",
            message = "TACTICAL PERK RETRIEVED: Unlocked '${s.name}'. Passive active: ${s.description}."
        ))
    }

    suspend fun rollLootCrate(): CosmeticEntity? = withContext(Dispatchers.IO) {
        val profile = db.userProfileDao().getUserProfileSync() ?: return@withContext null
        val cost = 80
        if (profile.goldCoins < cost) return@withContext null

        // Deduct coins
        db.userProfileDao().updateUserProfile(profile.copy(goldCoins = profile.goldCoins - cost))

        // Gacha
        val allC = db.cosmeticDao().getAllCosmetics().firstOrNull() ?: emptyList()
        val locked = allC.filter { !it.isUnlocked }

        if (locked.isNotEmpty()) {
            val randomItem = locked.random()
            db.cosmeticDao().updateCosmetic(randomItem.copy(isUnlocked = true))
            
            db.chatLogDao().insertChatLog(ChatLogEntity(
                sender = "COACH",
                message = "CRATE UNLOCKED: Obtained rarity ${randomItem.rarity} gear: '${randomItem.name}'. Equip are inside the customization station."
            ))
            return@withContext randomItem
        } else {
            // Refund random gold or give extra XP
            updateProfileWithRewards(profile, earnedXp = 100, earnRr = 5, earnCoins = 40)
            db.chatLogDao().insertChatLog(ChatLogEntity(
                sender = "COACH",
                message = "CRATE DUPLICATE REFUND: All gear unlocked. XP and partial currency refund complete."
            ))
            return@withContext null
        }
    }

    suspend fun insertChatMessage(message: String) = withContext(Dispatchers.IO) {
        db.chatLogDao().insertChatLog(ChatLogEntity(sender = "PLAYER", message = message))
    }

    suspend fun insertCoachMessage(message: String) = withContext(Dispatchers.IO) {
        db.chatLogDao().insertChatLog(ChatLogEntity(sender = "COACH", message = message))
    }

    suspend fun cleanCompletedMissions() = withContext(Dispatchers.IO) {
        db.missionDao().deleteCompletedMissions()
    }

    suspend fun deleteMission(mission: MissionEntity) = withContext(Dispatchers.IO) {
        db.missionDao().deleteMission(mission)
    }

    suspend fun chooseClass(charClass: String) = withContext(Dispatchers.IO) {
        val profile = db.userProfileDao().getUserProfileSync() ?: return@withContext
        saveAndSyncProfile(profile.copy(characterClass = charClass))

        db.chatLogDao().insertChatLog(ChatLogEntity(
            sender = "COACH",
            message = "CLASS COMPILATION SECURED: Operative archetype recompiled to '$charClass'. Passive bonuses online."
        ))
    }

    // --- Login, Profile Settings and Admin Controls ---

    suspend fun saveAndSyncProfile(profile: UserProfileEntity) = withContext(Dispatchers.IO) {
        db.userProfileDao().updateUserProfile(profile)
        if (profile.email.isNotEmpty()) {
            val account = db.userAccountDao().getAccountByEmail(profile.email)
            if (account != null) {
                db.userAccountDao().updateAccount(copyProfileToAccount(profile, account))
            }
        }
    }

    suspend fun getAccountByEmail(email: String): UserAccountEntity? = withContext(Dispatchers.IO) {
        db.userAccountDao().getAccountByEmail(email)
    }

    suspend fun registerUser(email: String, password: String): Boolean = withContext(Dispatchers.IO) {
        val existing = db.userAccountDao().getAccountByEmail(email)
        if (existing != null) return@withContext false

        // Generate a unique identifier like ASC-XXXXXX
        val randomBits = (100000..999999).random()
        val userId = "ASC-$randomBits"

        // Default username is "ayanøkoji" as requested
        val newAccount = UserAccountEntity(
            email = email,
            password = password,
            username = "ayanøkoji",
            userId = userId,
            bio = "I seek to climb to the apex of human discipline."
        )
        db.userAccountDao().insertAccount(newAccount)

        // Seed some standard values into the active UserProfile row
        val baseProfile = UserProfileEntity(
            id = 1,
            email = email,
            password = password,
            username = "ayanøkoji",
            userId = userId,
            isLoggedIn = true,
            rememberDevice = true
        )
        db.userProfileDao().insertUserProfile(baseProfile)

        db.chatLogDao().insertChatLog(ChatLogEntity(
            sender = "COACH",
            message = "AUTHENTICATION MATRIX: New operative identity compiled under $email. Grid Access SECURED."
        ))
        true
    }

    suspend fun loginUser(email: String, password: String, rememberDevice: Boolean): Boolean = withContext(Dispatchers.IO) {
        val account = db.userAccountDao().getAccountByEmail(email) ?: return@withContext false
        if (account.password != password) return@withContext false

        // Save current active profile (id=1), which might belong to an old logged-in session, back to their account first!
        val activeProfile = db.userProfileDao().getUserProfileSync()
        if (activeProfile != null && activeProfile.isLoggedIn && activeProfile.email.isNotEmpty()) {
            val oldAccountOfActive = db.userAccountDao().getAccountByEmail(activeProfile.email)
            if (oldAccountOfActive != null) {
                db.userAccountDao().updateAccount(copyProfileToAccount(activeProfile, oldAccountOfActive))
            }
        }

        // Copy account stats into active profile (id=1)
        val loadedProfile = copyAccountToProfile(account, rememberDevice)
        db.userProfileDao().insertUserProfile(loadedProfile)

        db.chatLogDao().insertChatLog(ChatLogEntity(
            sender = "COACH",
            message = "WELCOME BACK: Identity confirmed for $email. Synchronizing historical focus cells..."
        ))
        true
    }

    suspend fun logoutUser() = withContext(Dispatchers.IO) {
        val activeProfile = db.userProfileDao().getUserProfileSync() ?: return@withContext
        if (activeProfile.isLoggedIn && activeProfile.email.isNotEmpty()) {
            val account = db.userAccountDao().getAccountByEmail(activeProfile.email)
            if (account != null) {
                db.userAccountDao().updateAccount(copyProfileToAccount(activeProfile, account))
            }
        }

        // Set isLoggedIn = false on active profile so login screen is displayed
        val loggedOutProfile = activeProfile.copy(
            isLoggedIn = false,
            email = "",
            password = ""
        )
        db.userProfileDao().insertUserProfile(loggedOutProfile)

        db.chatLogDao().insertChatLog(ChatLogEntity(
            sender = "COACH",
            message = "OPERATIVE DISCONNECTED: Sessions closed. Lock and load for next shift."
        ))
    }

    suspend fun updateActiveProfileSettings(
        username: String,
        bio: String,
        customAvatarUri: String,
        bannerImage: String,
        appTheme: String,
        soundEffectsEnabled: Boolean,
        animationIntensity: String,
        notificationsEnabled: Boolean
    ) = withContext(Dispatchers.IO) {
        val activeProfile = db.userProfileDao().getUserProfileSync() ?: return@withContext
        val updatedProfile = activeProfile.copy(
            username = username,
            bio = bio,
            customAvatarUri = customAvatarUri,
            bannerImage = bannerImage,
            appTheme = appTheme,
            soundEffectsEnabled = soundEffectsEnabled,
            animationIntensity = animationIntensity,
            notificationsEnabled = notificationsEnabled
        )
        db.userProfileDao().updateUserProfile(updatedProfile)

        // Sync with account if logged in
        if (updatedProfile.email.isNotEmpty()) {
            val account = db.userAccountDao().getAccountByEmail(updatedProfile.email)
            if (account != null) {
                db.userAccountDao().updateAccount(copyProfileToAccount(updatedProfile, account))
            }
        }
    }

    suspend fun resetProgress() = withContext(Dispatchers.IO) {
        val activeProfile = db.userProfileDao().getUserProfileSync() ?: return@withContext
        val resetedProfile = activeProfile.copy(
            level = 1,
            xp = 0,
            xpToNextLevel = 100,
            rank = "Recruit",
            division = 1,
            rankRating = 20,
            streak = 0,
            goldCoins = 50,
            skillPoints = 0,
            momentum = 0.5f,
            burnout = 0.1f,
            totalFocusMinutes = 0,
            dailyRoutineStreakCount = 0,
            dailyPushUps = "PENDING",
            dailySitUps = "PENDING",
            dailySquats = "PENDING",
            dailyRun = "PENDING"
        )
        db.userProfileDao().updateUserProfile(resetedProfile)

        if (resetedProfile.email.isNotEmpty()) {
            val account = db.userAccountDao().getAccountByEmail(resetedProfile.email)
            if (account != null) {
                db.userAccountDao().updateAccount(copyProfileToAccount(resetedProfile, account))
            }
        }

        db.chatLogDao().insertChatLog(ChatLogEntity(
            sender = "COACH",
            message = "SYSTEM WIPE EXECUTED: All rating tables and training logs have been completely reset."
        ))
    }

    suspend fun backupData() = withContext(Dispatchers.IO) {
        val activeProfile = db.userProfileDao().getUserProfileSync() ?: return@withContext
        if (activeProfile.email.isNotEmpty()) {
            val account = db.userAccountDao().getAccountByEmail(activeProfile.email)
            if (account != null) {
                db.userAccountDao().updateAccount(copyProfileToAccount(activeProfile, account))
            }
        }
        db.chatLogDao().insertChatLog(ChatLogEntity(
            sender = "COACH",
            message = "CLOUD BACKUP COMPILATION: Stored profile snapshots in secure sector."
        ))
    }

    suspend fun exportDataToText(): String = withContext(Dispatchers.IO) {
        val profile = db.userProfileDao().getUserProfileSync() ?: return@withContext "No Profile Loaded."
        buildString {
            appendLine("=== ASCEND RANKED FOCUS OPERATIONS LOG VALUE ===")
            appendLine("EXPORT TIMESTAMP : ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US).format(java.util.Date())}")
            appendLine("SYSTEM ID        : ${profile.userId}")
            appendLine("OPERATIVE EMAIL  : ${profile.email}")
            appendLine("USERNAME         : ${profile.username}")
            appendLine("BIOGRAPHY        : ${profile.bio}")
            appendLine("------------------------------------------")
            appendLine("RANK STANDING    : ${profile.rank.uppercase()} (Division ${profile.division})")
            appendLine("RANK RATING (RR) : ${profile.rankRating}/100")
            appendLine("EXPERIENCE (XP)  : Level ${profile.level} (${profile.xp}/${profile.xpToNextLevel} XP)")
            appendLine("GOLD COINS       : ${profile.goldCoins} CC")
            appendLine("SKILL POINTS     : ${profile.skillPoints} SP")
            appendLine("STREAK METRIC    : ${profile.streak} active operations")
            appendLine("BIOLOGICAL STREK : ${profile.dailyRoutineStreakCount} routine streak")
            appendLine("FOCUS METRICS    : ${profile.totalFocusMinutes} total focused minutes")
            appendLine("COMPLETION RATE  : ${(profile.completionRate * 100).toInt()}%")
            appendLine("WEEKLY SCORE     : ${profile.weeklyPerformanceScore}/100")
            appendLine("------------------------------------------")
            appendLine("DAILY ROUTINE VECTORS:")
            appendLine("  [PUSH-UPS]     : ${profile.dailyPushUps}")
            appendLine("  [SIT-UPS]      : ${profile.dailySitUps}")
            appendLine("  [SQUATS]       : ${profile.dailySquats}")
            appendLine("  [10KM RUN]     : ${profile.dailyRun}")
            appendLine("==========================================")
        }
    }

    private fun copyProfileToAccount(profile: UserProfileEntity, account: UserAccountEntity): UserAccountEntity {
        return account.copy(
            username = profile.username,
            userId = profile.userId,
            bio = profile.bio,
            customAvatarUri = profile.customAvatarUri,
            bannerImage = profile.bannerImage,
            appTheme = profile.appTheme,
            soundEffectsEnabled = profile.soundEffectsEnabled,
            animationIntensity = profile.animationIntensity,
            notificationsEnabled = profile.notificationsEnabled,
            rememberDevice = profile.rememberDevice,
            level = profile.level,
            xp = profile.xp,
            xpToNextLevel = profile.xpToNextLevel,
            rank = profile.rank,
            division = profile.division,
            rankRating = profile.rankRating,
            streak = profile.streak,
            goldCoins = profile.goldCoins,
            skillPoints = profile.skillPoints,
            momentum = profile.momentum,
            totalFocusMinutes = profile.totalFocusMinutes,
            dailyRoutineStreakCount = profile.dailyRoutineStreakCount,
            dailyPushUps = profile.dailyPushUps,
            dailySitUps = profile.dailySitUps,
            dailySquats = profile.dailySquats,
            dailyRun = profile.dailyRun,
            weeklyPerformanceScore = profile.weeklyPerformanceScore,
            completionRate = profile.completionRate
        )
    }

    private fun copyAccountToProfile(account: UserAccountEntity, rememberDevice: Boolean): UserProfileEntity {
        return UserProfileEntity(
            id = 1,
            email = account.email,
            password = account.password,
            username = account.username,
            userId = account.userId,
            bio = account.bio,
            customAvatarUri = account.customAvatarUri,
            bannerImage = account.bannerImage,
            appTheme = account.appTheme,
            soundEffectsEnabled = account.soundEffectsEnabled,
            animationIntensity = account.animationIntensity,
            notificationsEnabled = account.notificationsEnabled,
            rememberDevice = rememberDevice,
            isLoggedIn = true,
            level = account.level,
            xp = account.xp,
            xpToNextLevel = account.xpToNextLevel,
            rank = account.rank,
            division = account.division,
            rankRating = account.rankRating,
            streak = account.streak,
            goldCoins = account.goldCoins,
            skillPoints = account.skillPoints,
            momentum = account.momentum,
            totalFocusMinutes = account.totalFocusMinutes,
            dailyRoutineStreakCount = account.dailyRoutineStreakCount,
            dailyPushUps = account.dailyPushUps,
            dailySitUps = account.dailySitUps,
            dailySquats = account.dailySquats,
            dailyRun = account.dailyRun,
            weeklyPerformanceScore = account.weeklyPerformanceScore,
            completionRate = account.completionRate
        )
    }
}
