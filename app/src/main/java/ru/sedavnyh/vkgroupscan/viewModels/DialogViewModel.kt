package ru.sedavnyh.vkgroupscan.viewModels

import androidx.lifecycle.ViewModel
import ru.sedavnyh.vkgroupscan.data.Repository
import ru.sedavnyh.vkgroupscan.di.Scopes
import ru.sedavnyh.vkgroupscan.models.entities.GroupEntity
import toothpick.Toothpick
import javax.inject.Inject

class DialogViewModel : ViewModel() {

    @Inject
    lateinit var repository: Repository

    init {
        Toothpick.inject(this, Toothpick.openScope(Scopes.APP_SCOPE))
    }

    suspend fun makeGroup(groupName: String): GroupEntity {
        val grResponse = repository.remote.groupsGetById(groupName)
        val postResponse = repository.remote.wallGet((grResponse.response?.get(0)!!.id!!*-1).toString(), "1", "0")
        return GroupEntity(
            grResponse.response[0].id!!*-1,
            postResponse?.count ?: 0,
            grResponse.response[0].name!!,
            grResponse.response[0].photo50!!
        )
    }
}