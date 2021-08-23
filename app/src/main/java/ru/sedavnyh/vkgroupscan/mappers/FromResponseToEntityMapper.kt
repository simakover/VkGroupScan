package ru.sedavnyh.vkgroupscan.mappers

import ru.sedavnyh.vkgroupscan.models.entities.PostEntity
import ru.sedavnyh.vkgroupscan.models.wallGetModel.Post
import javax.inject.Inject

class FromResponseToEntityMapper @Inject constructor() {
    fun mapToPostEntity(post: Post): PostEntity {
        val images: MutableList<String> = mutableListOf()
        var titles: MutableList<String> = mutableListOf()

        if (post.text!!.isNotEmpty()) {
            titles.add(post.text.orEmpty())
        }
        post.attachments?.map { attach ->
            if (attach.type == "photo") {
                attach.photo?.sizes?.map { size ->
                    if (size.type == "x") {
                        size.url?.let {
                            images.add(it)
                        }
                    }
                }
            }
            if (attach.type == "album") {
                titles.add(attach.album?.title.orEmpty())

                attach.album?.thumb?.sizes?.map { size ->
                    if (size.type == "x") {
                        size.url?.let {
                            images.add(it)
                        }
                    }
                }
            }
            if (attach.type == "link") {
                titles.add(attach.link?.title.orEmpty())

                attach.link?.photo?.sizes?.map { size ->
                    if (size.type == "x") {
                        size.url?.let {
                            images.add(it)
                        }
                    }
                }
            }

        }
        return PostEntity(
            id = post.id ?: 0,
            date = post.date ?: 0,
            ownerId = post.ownerId ?: 0,
            isPinned = post.isPinned ?: 0,
            markedAsAds = post.markedAsAds ?: 0,
            text = post.text.orEmpty(),
            groupAvatar = post.groupAvatar.orEmpty(),
            groupName = post.groupName.orEmpty(),
            totalComments = titles.toMutableSet().toMutableList(),
            images = images
        )
    }
}