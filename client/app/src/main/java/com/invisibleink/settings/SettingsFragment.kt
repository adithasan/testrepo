package com.invisibleink.settings

import android.os.Bundle
import android.view.ViewGroup
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.invisibleink.R
import com.invisibleink.dashboard.NavigationActivity
import com.invisibleink.explore.vote.VoteGateway
import com.invisibleink.explore.vote.createVoteDatabase
import com.invisibleink.extensions.doNothingOnBackPress
import com.invisibleink.extensions.showSnackbar
import com.invisibleink.injection.InvisibleInkApplication
import com.invisibleink.report.ReportDialogBuilder
import com.invisibleink.report.ReportGateway
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class SettingsFragment : PreferenceFragmentCompat(), NavigationActivity.BackPressHandler {

    @Inject
    lateinit var voteGateway: VoteGateway
    @Inject
    lateinit var reportGateway: ReportGateway

    private val disposable = CompositeDisposable()

    override fun onBackPress() = doNothingOnBackPress()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (requireContext().applicationContext as InvisibleInkApplication).appComponent.inject(this)
        voteGateway.voteDatabase =
            createVoteDatabase(application = requireActivity().application)

        findPreference<Preference>(requireContext().getString(R.string.pref_category_vote_db_clear_key))?.setOnPreferenceClickListener {
            clearVotes()
            true
        }

        findPreference<Preference>(requireContext().getString(R.string.pref_category_vote_db_upvote_key))?.setOnPreferenceClickListener {
            upvoteNote()
            true
        }

        findPreference<Preference>(requireContext().getString(R.string.pref_category_vote_db_downvote_key))?.setOnPreferenceClickListener {
            downvoteNote()
            true
        }

        findPreference<Preference>(requireContext().getString(R.string.pref_category_vote_db_load_all_key))?.setOnPreferenceClickListener {
            logAllVotes()
            true
        }

        findPreference<Preference>(requireContext().getString(R.string.pref_category_report_key))?.setOnPreferenceClickListener {
            reportNote()
            true
        }
    }

    override fun onStop() {
        super.onStop()
        disposable.dispose()
    }

    private fun reportNote() {
        ReportDialogBuilder().buildReportDialog(requireContext(),
            { reportType, reportComment ->
                if (reportType != null) {
                    disposable.add(reportGateway.reportNote(
                        noteId = "b56d6a1b-2b1d-4f72-b50e-36474a2ba02e",
                        reportType = reportType,
                        comment = reportComment
                    )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            { showMessage(it.toString()) },
                            { showMessage("Error issuing report: $it") }
                        ))
                } else {
                    showMessage("No report selected")
                }
            },
            { showMessage("Report cancelled") }
        ).show()
    }

    private fun upvoteNote() {
        disposable.add(
            voteGateway.upVoteNote("b56d6a1b-2b1d-4f72-b50e-36474a2ba02e")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { showMessage(it.toString()) },
                    { showMessage("Error issuing upvote: $it") }
                )
        )
    }

    private fun downvoteNote() {
        disposable.add(
            voteGateway.downVoteNote("b56d6a1b-2b1d-4f72-b50e-36474a2ba02e")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { showMessage(it.toString()) },
                    { showMessage("Error issuing downvote: $it") }
                )
        )
    }

    private fun clearVotes() = voteGateway.clearDatabase()

    private fun logAllVotes() = showMessage(voteGateway.printNotes())

    private fun showMessage(msg: String) = rootView().showSnackbar(msg)

    private fun rootView() = requireActivity().findViewById<ViewGroup>(R.id.fragmentContainer)
}
