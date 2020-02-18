package com.invisibleink.note

import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.annotation.StringRes
import com.invisibleink.R
import com.invisibleink.architecture.BaseViewDelegate
import com.invisibleink.architecture.ViewProvider
import kotlinx.android.synthetic.main.fragment_note.view.*

class NoteViewDelegate(viewProvider: ViewProvider) :
    BaseViewDelegate<NoteViewState, NoteViewEvent, NoteDestination>(viewProvider) {

    private val title: EditText = viewProvider.findViewById(R.id.title)
    private val body: EditText = viewProvider.findViewById(R.id.body)
    private val addPhotoButton: ImageButton = viewProvider.findViewById(R.id.addPhoto)
    private val uploadButton: Button = viewProvider.findViewById(R.id.uploadButton)
    private val expirationButton: Button = viewProvider.findViewById(R.id.expirationButton)

    init {
        uploadButton.setOnClickListener { pushEvent(NoteViewEvent.Upload(composeNote())) }
        expirationButton.setOnClickListener { showDatePicker() }
        addPhotoButton.setOnClickListener { showAddPhoto() }
    }

    override fun render(viewState: NoteViewState): Unit? = when (viewState) {
        is NoteViewState.Empty -> clearNoteContent()
        is NoteViewState.Draft -> showDraftContent(viewState.draft)
        is NoteViewState.InvalidNote -> showErrorMessage(viewState.message)
        is NoteViewState.UploadError -> showErrorMessage(viewState.message)
        is NoteViewState.UploadSuccess -> showSuccessMessage(viewState.message)
    }

    private fun clearNoteContent() = Unit // TODO: Ensure all the note fields are cleared

    private fun showDraftContent(note: Note) = Unit // TODO: Update all the fields for the draft

    private fun showErrorMessage(@StringRes message: Int) = Unit // TODO: Display the message

    private fun showSuccessMessage(@StringRes message: Int) = Unit // TODO: Display the message

    private fun showDatePicker() = Unit // TODO: Display a date picker

    private fun showAddPhoto() = Unit // TODO: Display with an intent chooser

    private fun composeNote(): Note = Note(title.text.toString(), body.text.toString())

}
