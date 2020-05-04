package jp.mamori_i.app.screen.trace

import jp.mamori_i.app.data.model.DeepContact

data class TraceHistoryListItem(val type: TraceHistoryAdapter.ViewType,
                                val sectionDate: String?,
                                val deepContact: DeepContact?)