package ru.sedavnyh.vkgroupscan.models.entities

import ru.sedavnyh.vkgroupscan.models.wallGetModel.Attachment

data class PostEntity (
    val id: Int?,
    val attachments: List<Attachment>?,
    val date: Int?,
    val ownerId: Int?,
    val isPinned: Int?,
    val markedAsAds: Int?,
    val text: String?,
    var groupAvatar: String?,
    var groupName: String?,
    var totalComments: String?
)