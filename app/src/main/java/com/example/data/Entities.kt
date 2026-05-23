package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val username: String = "RECRUIT_ONE",
    val characterClass: String = "Scholar", // Warrior, Scholar, Strategist, Creator, Monk, Hybrid
    val level: Int = 1,
    val xp: Int = 0,
    val xpToNextLevel: Int = 100,
    val rank: String = "Recruit", // Recruit, Bronze, Silver, Gold, Elite, Apex, Mythic
    val division: Int = 1, // 1 to 3
    val rankRating: Int = 20, // RR 0 - 100
    val streak: Int = 0,
    val prestige: Int = 0,
    val battlePassXp: Int = 0,
    val skillPoints: Int = 0,
    val momentum: Float = 0.5f, // Range 0.0 to 1.0 (based on daily completions)
    val burnout: Float = 0.1f, // Range 0.0 to 1.0 (based on work without rest)
    val goldCoins: Int = 50,
    val equippedTitle: String = "Vanguard of Self",
    val equippedOutfit: String = "Starter Cloak", // Starter Cloak, Cyber Jacket, Neon Armor, Apex Aura, Mythic Wings
    val equippedWeapon: String = "Focuser Pen", // Focuser Pen, Gym Dumbbell, Code Katana, Monk Beads, Neon Sabre
    val equippedPet: String = "None", // Hologram Drone, Cyber Kitten, Cyber Dragon, None
    val equippedBackground: String = "Slate Dark", // Dark Cyber, Eclipse Red, Cosmic Nebula
    val totalFocusMinutes: Long = 0,
    val lastActiveTimestamp: Long = System.currentTimeMillis(),
    
    // Daily Routine Tracking
    val dailyPushUps: String = "PENDING", // PENDING, COMPLETED, FAILED
    val dailySitUps: String = "PENDING",  // PENDING, COMPLETED, FAILED
    val dailySquats: String = "PENDING",  // PENDING, COMPLETED, FAILED
    val dailyRun: String = "PENDING",     // PENDING, COMPLETED, FAILED
    val dailyRoutineStreakCount: Int = 0,
    
    // Profiles, Settings and Login Credentials Info
    val userId: String = "ASC-57A19B",
    val bio: String = "I seek to climb to the apex of human discipline.",
    val customAvatarUri: String = "avatar_1", // avatar_1, avatar_2, avatar_3, etc.
    val bannerImage: String = "Slate Dark", // Dark Cyber, Eclipse Red, Cosmic Nebula, Neon Green, Twilight Purple
    val appTheme: String = "Dark Cyber", // Dark Cyber, Eclipse Red, Neon Green, Twilight Purple
    val soundEffectsEnabled: Boolean = true,
    val animationIntensity: String = "Medium", // Low, Medium, High
    val notificationsEnabled: Boolean = true,
    val isLoggedIn: Boolean = false,
    val rememberDevice: Boolean = true,
    val email: String = "",
    val password: String = "",
    val weeklyPerformanceScore: Int = 85,
    val completionRate: Float = 0.90f
)

@Entity(tableName = "missions")
data class MissionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String, // Fitness, Study, Work, Deep Focus, Creativity, Coding, Business, Skill, Discipline, Mental Health
    val rarity: String = "Common", // Common, Rare, Epic, Legendary, Mythic
    val difficulty: String = "Medium", // Easy, Medium, Hard, Legendary
    val durationMinutes: Int = 30,
    val xpReward: Int = 20,
    val rrReward: Int = 12,
    val failurePenalty: Int = 10, // RR lost on omission
    val isCompleted: Boolean = false,
    val isSideQuest: Boolean = false, // Random/surprise popup quest
    val isSecret: Boolean = false,
    val isSpecialQuest: Boolean = false, // Rare/long-term high difficulty mission
    val isDailyTask: Boolean = true, // Standard daily mission
    val creationTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "boss_battles")
data class BossEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String, // e.g., "90-Day Transformation", "CS Final Exam", "Startup Launch v1"
    val description: String,
    val currentHealth: Float = 100f,
    val maxHealth: Float = 100f,
    val isCompleted: Boolean = false,
    val rewardTitle: String = "Boss Slayer",
    val rewardItem: String = "Excalibur Sword",
    val xpReward: Int = 500,
    val goldReward: Int = 100,
    val dateCreated: Long = System.currentTimeMillis()
)

@Entity(tableName = "skills")
data class SkillEntity(
    @PrimaryKey val id: String, // e.g., "discipline_decay", "xp_booster"
    val name: String,
    val category: String, // Discipline, Focus, Intellect, Physical, Spirit
    val description: String,
    val effectText: String,
    val isUnlocked: Boolean = false,
    val costSkillPoints: Int = 1,
    val parentSkillId: String? = null
)

@Entity(tableName = "cosmetics")
data class CosmeticEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String, // outfit, weapon, pet, background, title
    val rarity: String, // Common, Rare, Epic, Legendary, Mythic
    val coinCost: Int,
    val isUnlocked: Boolean = false,
    val isEquipped: Boolean = false,
    val description: String = ""
)

@Entity(tableName = "chat_log")
data class ChatLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sender: String, // "COACH" or "PLAYER"
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_accounts")
data class UserAccountEntity(
    @PrimaryKey val email: String,
    val password: String,
    val username: String = "ayanøkoji",
    val userId: String = "ASC-57A19B",
    val bio: String = "I seek to climb to the apex of human discipline.",
    val customAvatarUri: String = "avatar_1",
    val bannerImage: String = "Slate Dark",
    val appTheme: String = "Dark Cyber",
    val soundEffectsEnabled: Boolean = true,
    val animationIntensity: String = "Medium",
    val notificationsEnabled: Boolean = true,
    val rememberDevice: Boolean = true,
    
    // Game profile progress stats copy
    val level: Int = 1,
    val xp: Int = 0,
    val xpToNextLevel: Int = 100,
    val rank: String = "Recruit",
    val division: Int = 1,
    val rankRating: Int = 20,
    val streak: Int = 0,
    val goldCoins: Int = 50,
    val skillPoints: Int = 0,
    val momentum: Float = 0.5f,
    val totalFocusMinutes: Long = 0,
    val dailyRoutineStreakCount: Int = 0,
    val dailyPushUps: String = "PENDING",
    val dailySitUps: String = "PENDING",
    val dailySquats: String = "PENDING",
    val dailyRun: String = "PENDING",
    val weeklyPerformanceScore: Int = 85,
    val completionRate: Float = 0.90f
)
