package com.tglee.tgaccount.ui.transferfeed.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tglee.tgaccount.core.feed.FeedItemUiState
import com.tglee.tgaccount.data.transferfeed.vo.FeedMyAccountMoreButtonVO
import com.tglee.tgaccount.data.transferfeed.vo.FeedMyAccountVO
import com.tglee.tgaccount.data.transferfeed.vo.FeedMyAccountVOList
import com.tglee.tgaccount.data.transferfeed.vo.FeedRecentRecipientVO
import com.tglee.tgaccount.data.transferfeed.vo.FeedRecentRecipientVOList
import com.tglee.tgaccount.data.transferfeed.vo.FeedSearchBarVO
import com.tglee.tgaccount.data.transferfeed.vo.FeedVO
import com.tglee.tgaccount.domain.transferfeed.usecase.LoadTransferFeedUseCase
import com.tglee.tgaccount.ui.transferfeed.TransferFeedUiState
import com.tglee.tgaccount.ui.transferfeed.uistate.MyAccountItemUiState
import com.tglee.tgaccount.ui.transferfeed.uistate.MyAccountMoreButtonUiState
import com.tglee.tgaccount.ui.transferfeed.uistate.MyAccountUiStateList
import com.tglee.tgaccount.ui.transferfeed.uistate.RecentRecipientUiState
import com.tglee.tgaccount.ui.transferfeed.uistate.RecentRecipientUiStateList
import com.tglee.tgaccount.ui.transferfeed.uistate.SearchBarUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class TransferScreenViewModel @Inject constructor(
    private val loadTransferFeedUseCase: LoadTransferFeedUseCase
): ViewModel() {

    val uiState: StateFlow<TransferFeedUiState> =
        loadTransferFeedUseCase.observe()
            .map<List<FeedVO>, TransferFeedUiState> {
                TransferFeedUiState.Loaded(it.toUiState())
            }.catch {
                emit(TransferFeedUiState.Error)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = TransferFeedUiState.Loading,
            )

}

private fun List<FeedVO>.toUiState() = map { vo ->
    when (vo) {
        is FeedSearchBarVO -> SearchBarUiState()
        is FeedMyAccountVOList -> MyAccountUiStateList(
            id = UUID.randomUUID().toString(),
            list = vo.list.map {
                MyAccountItemUiState(
                    id = UUID.randomUUID().toString(),
                    accountName = it.accountName,
                    accountNumber = it.accountNumber,
                    bankName = it.bankName,
                    iconUrl = it.iconUrl
                )
            },
        )
        is FeedMyAccountMoreButtonVO -> MyAccountMoreButtonUiState(
            id = UUID.randomUUID().toString(),
            expanded = vo.expanded,
            hiddenCount = 0, // TODO
        )
        is FeedRecentRecipientVOList -> RecentRecipientUiStateList(
            id = UUID.randomUUID().toString(),
            list = vo.list.map {
                when(it) {
                    is FeedRecentRecipientVO.Account -> RecentRecipientUiState.Account(
                        id = UUID.randomUUID().toString(),
                        name = it.name,
                        accountNumber = it.accountNumber,
                        bankName = it.bankName,
                        iconUrl = it.iconUrl,
                        justSent = false // TODO
                    )

                    is FeedRecentRecipientVO.Phone -> RecentRecipientUiState.Phone(
                        id = UUID.randomUUID().toString(),
                        name = it.name,
                        phoneNumber = it.phoneNumber,
                        iconUrl = it.iconUrl,
                        justSent = false // TODO
                    )

                    FeedRecentRecipientVO.None -> RecentRecipientUiState.None(
                        id = UUID.randomUUID().toString(),
                    )
                }
            } as List<RecentRecipientUiState>
        )
        else -> error("unknown FeedVO: $vo")
    }
}