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
import kotlin.jvm.java

class MainActivity : AppCompatActivity() {

    // Utilizamos o ViewBinding (ActivityMainBinding) em vez do antigo e custoso 'findViewById'.
    // A principal vantagem arquitetural aqui é a Segurança de Tipo (Type Safety) e Segurança contra Nulos (Null Safety).
    // O compilador já mapeia todos os IDs do XML, impedindo que o app quebre por tentar acessar um botão que não existe.
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: FolderAdapter

    // O 'by viewModels' é um delegate do Kotlin. Ele delega ao ciclo de vida da Activity
    // a responsabilidade de criar ou recuperar o ViewModel. Se a tela girar, a Activity é recriada,
    // mas o ViewModel permanece intacto na memória.
    private val viewModel: FolderViewModel by viewModels {
        AppViewModelFactory(
            (application as LinkBoxApplication).folderRepository,
            (application as LinkBoxApplication).linkRepository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflamos o layout XML e amarramos a interface à lógica Kotlin.
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeViewModel()

        binding.fabAddFolder.setOnClickListener {
            showFolderDialog(null)
        }
    }

    private fun setupRecyclerView() {
        // O RecyclerView é o componente padrão do Android para listas extensas.
        // Diferente do antigo ListView, ele 'recicla' as caixas visuais (Views) que saem da tela
        // quando o usuário rola a lista, reaproveitando-as para os itens que estão entrando.
        // Isso consome uma fração mínima da memória RAM do celular.
        adapter = FolderAdapter(
            onFolderClick = { folder ->
                // Navega para a tela de Links enviando os dados da pasta
                val intent = android.content.Intent(this, com.ifpb.linkbox_android.ui.links.LinksActivity::class.java).apply {
                    // Usamos o 'putExtra' como um envelope para transportar dados entre as telas.
                    // Estamos enviando o ID e o Nome da pasta para que a próxima tela saiba o que consultar no banco.
                    putExtra("EXTRA_FOLDER_ID", folder.id)
                    putExtra("EXTRA_FOLDER_NAME", folder.name)
                }
                startActivity(intent)
            },
            onMenuClick = { view, folder ->
                showPopupMenu(view, folder)
            }
        )
        // O LinearLayoutManager define que a lista será desenhada de cima para baixo (verticalmente).
        binding.rvFolders.layoutManager = LinearLayoutManager(this)
        binding.rvFolders.adapter = adapter
    }

    private fun observeViewModel() {
        // lifecycleScope atrela a execução da corrotina ao tempo de vida da Activity.
        lifecycleScope.launch {
            // repeatOnLifecycle(STARTED) é o padrão ouro atual do Android:
            // A tela só escuta as mudanças do banco de dados enquanto o app estiver visível.
            // Se o usuário minimizar o app, a escuta pausa, evitando crashes se o banco atualizar no fundo.
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allFolders.collect { folders ->
                    // Manda a lista nova para o Adapter. O DiffUtil faz o resto do trabalho.
                    adapter.submitList(folders)

                    // Lógica simples de UI State para mostrar ou esconder a mensagem de vazio
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

    // Em vez de criar uma Activity (tela inteira) separada apenas para digitar o nome de uma pasta,
    // implementei um AlertDialog. Isso mantém o usuário no contexto da aplicação e deixa o app mais leve.
    private fun showFolderDialog(folder: Folder?) {
        val isEditing = folder != null
        val title = if (isEditing) "Editar Pasta" else "Nova Pasta"

        // Criamos o campo de texto via código (programaticamente), sem precisar de um XML extra.
        val input = EditText(this)
        input.hint = "Nome da pasta"
        if (isEditing) input.setText(folder.name)

        // Usamos o Builder Pattern (Padrão Construtor) do AlertDialog para montar a janela modal.
        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(input)
            .setPositiveButton("Salvar") { _, _ ->
                val newName = input.text.toString()
                if (newName.isNotBlank()) {
                    // Se estivermos editando, usamos o método .copy() nativo das Data Classes do Kotlin,
                    // que cria um clone do objeto alterando apenas o atributo especificado (name).
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