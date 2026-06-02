package com.tarantino.linkkeeper

import javax.inject.Inject

class CreateGroupUseCase @Inject constructor(
    private val groupRepository: GroupRepository
) {
    suspend operator fun invoke(name: String, colorHex: String, iconName: String, isSecret: Boolean): Long {
        val group = Group(
            name = name,
            colorHex = colorHex,
            iconName = iconName,
            isSecret = isSecret
        )
        return groupRepository.insertGroup(group)
    }
}
