package com.ifpb.linkbox_android.ui.links

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ifpb.linkbox_android.LinkBoxApplication
import com.ifpb.linkbox_android.data.model.Link
import com.ifpb.linkbox_android.databinding.ActivityLinkFormBinding
import com.ifpb.linkbox_android.ui.viewmodel.AppViewModelFactory
import com.ifpb.linkbox_android.ui.viewmodel.LinkViewModel

class LinkFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLinkFormBinding

    private var folderId: Long = -1L
    private var linkId: Long = -1L // Se for diferente de -1, estamos em modo de Edição

    private val viewModel: LinkViewModel by viewModels {
        AppViewModelFactory(
            (application as LinkBoxApplication).folderRepository,
            (application as LinkBoxApplication).linkRepository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLinkFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recupera os dados da Intent
        folderId = intent.getLongExtra("EXTRA_FOLDER_ID", -1L)
        linkId = intent.getLongExtra("EXTRA_LINK_ID", -1L)

        setupUI()

        binding.btnSave.setOnClickListener {
            saveLink()
        }
    }

    private fun setupUI() {
        if (linkId != -1L) {
            supportActionBar?.title = "Editar Link"
            // Modo edição: preenche os campos com os dados recebidos
            binding.etTitle.setText(intent.getStringExtra("EXTRA_LINK_TITLE"))
            binding.etUrl.setText(intent.getStringExtra("EXTRA_LINK_URL"))
            binding.etDescription.setText(intent.getStringExtra("EXTRA_LINK_DESC"))
        } else {
            supportActionBar?.title = "Novo Link"
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun saveLink() {
        val title = binding.etTitle.text.toString().trim()
        val url = binding.etUrl.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()

        // Remove erros anteriores
        binding.tilTitle.error = null
        binding.tilUrl.error = null

        // Validações
        if (title.isEmpty()) {
            binding.tilTitle.error = "O título é obrigatório"
            return
        }
        if (url.isEmpty()) {
            binding.tilUrl.error = "A URL é obrigatória"
            return
        }
        if (folderId == -1L) {
            Toast.makeText(this, "Erro: Pasta não identificada.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Se passar nas validações, cria o objeto Link
        val link = Link(
            id = if (linkId == -1L) 0 else linkId, // ID 0 faz o Room auto-gerar um novo
            title = title,
            url = url,
            description = description.ifEmpty { null },
            folderId = folderId
        )

        // Salva ou atualiza
        if (linkId == -1L) {
            viewModel.insert(link)
        } else {
            viewModel.update(link)
        }

        Toast.makeText(this, "Link salvo com sucesso!", Toast.LENGTH_SHORT).show()
        finish() // Fecha a tela de formulário e volta para a lista
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}