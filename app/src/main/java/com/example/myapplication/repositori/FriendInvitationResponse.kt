package com.example.myapplication.repositori

import com.example.myapplication.model.FriendInvitedWithSender
import com.example.myapplication.model.FriendsInvitation
import com.example.myapplication.model.User


sealed class FriendInvitationResponse {
    data class FriendInvited(val message: String, val newInvited:FriendsInvitation): FriendInvitationResponse()
    data class AcceptFriendInvited(val message: String, val invited:FriendsInvitation): FriendInvitationResponse()
    data class UnfriendInvited(val message: String): FriendInvitationResponse()

    data class GetFriend (val friend:List<User> = emptyList() ):FriendInvitationResponse()
    data class GetFriendInvited(val friend:List<FriendInvitedWithSender> = emptyList()):FriendInvitationResponse()
}