package com.ifpb.linkbox_android

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.ifpb.linkbox_android.data.model.Folder
import com.ifpb.linkbox_android.databinding.ActivityMainBinding
import com.ifpb.linkbox_android.ui.folders.FolderAdapter
import com.ifpb.linkbox_android.ui.viewmodel.AppViewModelFactory
import com.ifpb.linkbox_android.ui.viewmodel.FolderViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding // Correção: faltava o 'var' aqui no exemplo anterior
    private lateinit var adapter: FolderAdapter       // Correção: faltava o 'var' aqui no exemplo anterior

    private val viewModel: FolderViewModel by viewModels {
        AppViewModelFactory(
            (application as LinkBoxApplication).folderRepository,
            (application as LinkBoxApplication).linkRepository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeViewModel()

        binding.fabAddFolder.setOnClickListener {
            showFolderDialog(null)
        }
    }

    private fun setupRecyclerView() {
        adapter = FolderAdapter(
            onFolderClick = { folder ->
                Toast.makeText(this, "Abrir pasta: ${folder.name}", Toast.LENGTH_SHORT).show()
            },
            onMenuClick = { view, folder ->
                showPopupMenu(view, folder)
            }
        )
        binding.rvFolders.layoutManager = LinearLayoutManager(this)
        binding.rvFolders.adapter = adapter
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allFolders.collect { folders ->
                    adapter.submitList(folders)
                    binding.tvEmptyState.visibility = if (folders.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }

    private fun showPopupMenu(view: View, folder: Folder) {
        val popup = PopupMenu(this, view)
        popup.menu.add(0, 1, 0, "Editar")
        popup.menu.add(0, 2, 1, "Excluir")

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                1 -> {
                    showFolderDialog(folder)
                    true
                }
                2 -> {
                    if (folder.id == 1L) {
                        Toast.makeText(this, "A pasta 'Geral' não pode ser excluída", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.delete(folder)
                    }
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun showFolderDialog(folder: Folder?) {
        val isEditing = folder != null
        val title = if (isEditing) "Editar Pasta" else "Nova Pasta"

        val input = EditText(this)
        input.hint = "Nome da pasta"
        if (isEditing) input.setText(folder.name)

        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(input)
            .setPositiveButton("Salvar") { _, _ ->
                val newName = input.text.toString()
                if (newName.isNotBlank()) {
                    if (isEditing) {
                        viewModel.update(folder!!.copy(name = newName))
                    } else {
                        viewModel.insert(Folder(name = newName))
                    }
                } else {
                    Toast.makeText(this, "O nome não pode estar vazio", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}