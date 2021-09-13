package ru.sedavnyh.vkgroupscan.util

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.dialog.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.sedavnyh.vkgroupscan.R
import ru.sedavnyh.vkgroupscan.activities.MainActivity
import ru.sedavnyh.vkgroupscan.models.entities.GroupEntity
import ru.sedavnyh.vkgroupscan.viewModels.DialogViewModel


class AddGroupDialog : DialogFragment() {

    lateinit var viewModel: DialogViewModel
    lateinit var group : GroupEntity

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater;
            val layout = inflater.inflate(R.layout.dialog, null)

            val button = layout.insertGroupButton
            button.isEnabled = false
            button.alpha = 0.3f

            viewModel = ViewModelProvider(this).get(DialogViewModel::class.java)

            layout.getGroup.setOnClickListener {
                GlobalScope.launch(Dispatchers.Main) {
                    try {
                        group = viewModel.makeGroup(layout.group_id.text.toString())
                        layout.post_count.setText(group.postCount.toString())
                        layout.group_name.setText(group.title)
                        button.isEnabled = true
                        button.alpha = 1f
                    } catch (e: Exception) {
                        layout.post_count.setText("Группа не найдена")
                        button.isEnabled = false
                        button.alpha = 0.3f
                    }
                }
            }

            layout.cancelButton.setOnClickListener {
                getDialog()?.cancel()
            }

            layout.insertGroupButton.setOnClickListener {
                GlobalScope.launch(Dispatchers.Main) {

                    group.postCount -= layout.post_count.text.toString().toInt()
                    group.title = layout.group_name.text.toString()
                    if (group.postCount < 0)
                        group.postCount = 0

                    val groups = viewModel.repository.local.selectGroups()
                    val ids: MutableList<Int> = mutableListOf()
                    groups.map {
                        ids.add(it.id)
                    }
                    if (group.id !in ids) {
                        viewModel.repository.local.insertGroup(group)
                        getDialog()?.cancel()

                        val intent = Intent(requireContext(), MainActivity::class.java)
                        requireActivity().startActivity(intent)
                        requireActivity().finishAffinity()
                    }
                    else {
                        getDialog()?.cancel()
                    }
                }
            }

            builder.setView(layout)
            builder.create()

        } ?: throw IllegalStateException("Activity cannot be null")
    }
}