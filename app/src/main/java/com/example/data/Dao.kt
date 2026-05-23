package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getUserProfileSync(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(user: UserProfileEntity)

    @Update
    suspend fun updateUserProfile(user: UserProfileEntity)
}

@Dao
interface MissionDao {
    @Query("SELECT * FROM missions ORDER BY creationTimestamp DESC")
    fun getAllMissions(): Flow<List<MissionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMission(mission: MissionEntity)

    @Update
    suspend fun updateMission(mission: MissionEntity)

    @Delete
    suspend fun deleteMission(mission: MissionEntity)

    @Query("DELETE FROM missions WHERE isCompleted = 1")
    suspend fun deleteCompletedMissions()

    @Query("SELECT * FROM missions WHERE id = :id")
    suspend fun getMissionById(id: Int): MissionEntity?
}

@Dao
interface BossDao {
    @Query("SELECT * FROM boss_battles ORDER BY dateCreated DESC")
    fun getAllBosses(): Flow<List<BossEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBoss(boss: BossEntity)

    @Update
    suspend fun updateBoss(boss: BossEntity)

    @Delete
    suspend fun deleteBoss(boss: BossEntity)

    @Query("SELECT * FROM boss_battles WHERE id = :id")
    suspend fun getBossById(id: Int): BossEntity?
}

@Dao
interface SkillDao {
    @Query("SELECT * FROM skills")
    fun getAllSkills(): Flow<List<SkillEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSkills(skills: List<SkillEntity>)

    @Update
    suspend fun updateSkill(skill: SkillEntity)
}

@Dao
interface CosmeticDao {
    @Query("SELECT * FROM cosmetics")
    fun getAllCosmetics(): Flow<List<CosmeticEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCosmetics(cosmetics: List<CosmeticEntity>)

    @Update
    suspend fun updateCosmetic(cosmetic: CosmeticEntity)

    @Query("UPDATE cosmetics SET isEquipped = 0 WHERE category = :category")
    suspend fun unequipAllInCategory(category: String)

    @Transaction
    suspend fun equipCosmetic(id: String, category: String) {
        unequipAllInCategory(category)
        // Set isEquipped = 1 for the target ID
        equipTarget(id)
    }

    @Query("UPDATE cosmetics SET isEquipped = 1 WHERE id = :id")
    suspend fun equipTarget(id: String)
}

@Dao
interface ChatLogDao {
    @Query("SELECT * FROM chat_log ORDER BY timestamp ASC")
    fun getChatLogs(): Flow<List<ChatLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatLog(log: ChatLogEntity)

    @Query("DELETE FROM chat_log")
    suspend fun clearChatLogs()
}

@Dao
interface UserAccountDao {
    @Query("SELECT * FROM user_accounts")
    fun getAllAccounts(): Flow<List<UserAccountEntity>>

    @Query("SELECT * FROM user_accounts WHERE email = :email LIMIT 1")
    suspend fun getAccountByEmail(email: String): UserAccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: UserAccountEntity)

    @Update
    suspend fun updateAccount(account: UserAccountEntity)

    @Delete
    suspend fun deleteAccount(account: UserAccountEntity)
}
