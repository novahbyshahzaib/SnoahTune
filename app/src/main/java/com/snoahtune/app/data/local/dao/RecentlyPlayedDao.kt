package com.snoahtune.app.data.local.dao

import androidx.room.*
import com.snoahtune.app.data.local.entities.RecentlyPlayedEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentlyPlayedDao {

    @Query("SELECT * FROM recently_played ORDER BY playedAt DESC LIMIT 20")
    fun getRecentlyPlayed(): Flow<List<RecentlyPlayedEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: RecentlyPlayedEntity)

    @Query(
        "DELETE FROM recently_played WHERE songId NOT IN " +
        "(SELECT songId FROM recently_played ORDER BY playedAt DESC LIMIT 20)"
    )
    suspend fun trimToLimit()
}
