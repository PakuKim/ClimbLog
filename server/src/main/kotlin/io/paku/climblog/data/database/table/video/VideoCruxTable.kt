package io.paku.climblog.data.database.table.video

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

internal object VideoCruxTable: LongIdTable("video_cruxes") {
    val videoId = reference("video_id", VideoTable, onDelete = ReferenceOption.CASCADE)
    val cruxStartTime = double("crux_start_time")
    val cruxEndTime = double("crux_end_time")
}
