package com.rcraggs.doubledose.activity

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.rcraggs.doubledose.R
import com.rcraggs.doubledose.model.Drug
import com.rcraggs.doubledose.ui.DrugCardAdapter
import com.rcraggs.doubledose.ui.MaterialTapTargetSequence
import com.rcraggs.doubledose.ui.getDoseDescription
import com.rcraggs.doubledose.util.defaultConfig
import com.rcraggs.doubledose.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.architecture.ext.viewModel
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt
import uk.co.samuelwall.materialtaptargetprompt.extras.backgrounds.FullscreenPromptBackground


class MainActivity : AppCompatActivity() {

    private val viewModel by viewModel<HomeViewModel>()
    private var inDemoMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(main_toolbar)

        viewModel.start()

        val adapter = DrugCardAdapter(viewModel.drugWithDoses,
                this::takeDoseNow,
                this::chooseDoseTime,
                this::showDrugHistory
                )
        rv_drugs.adapter = adapter
        rv_drugs.layoutManager = LinearLayoutManager(this)

        viewModel.drugWithDoses.observe(this,
            Observer {
                adapter.notifyDataSetChanged()
            })

        viewModel.timer.observe(this,
            Observer {
                adapter.notifyDataSetChanged()
            })

    }


    private fun showDrugHistory(drug: Drug) {
        if (inDemoMode) return
        val intent = Intent(this, HistoryActivity::class.java)
        intent.putExtra( HistoryActivity.HISTORY_ACTIVITY_EXTRA_DRUG_ID, drug.id)
        startActivity(intent)
    }

    private fun chooseDoseTime(drug: Drug) {

        if (inDemoMode) return

        val newFragment = TimePickerFragment()

        // Set the drug type
        val bundle = Bundle()
        bundle.putLong(TimePickerFragment.ARG_DRUG_TYPE, drug.id)
        newFragment.arguments = bundle
        newFragment.show(fragmentManager, "timepicker")
    }

    @CallSuper
    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {

            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_update_drugs -> {
                val intent = Intent(this, DrugListActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_show_history -> {
                val intent = Intent(this, HistoryActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_intro-> {

                OldTapTargetThing()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun NextTapTargetThing() {

        val drugCard = getFirstDrugCard()

        val introStepCount = "5"
        var stepCount = 0

        val promptList = ArrayList<MaterialTapTargetPrompt.Builder>()
        var promptIndex = 0

        inDemoMode = true

        val sequence = MaterialTapTargetSequence()

        sequence.add(MaterialTapTargetPrompt.Builder(this)
                .setTarget(drugCard.getDrugInfoView())
                .setClipToView(drugCard.getCard())
                .setPrimaryText("HUCK")
                .setSecondaryText("dfsdfdf")
                .setPromptBackground(FullscreenPromptBackground()))

        sequence.add(MaterialTapTargetPrompt.Builder(this)
                .setTarget(drugCard.getDoseNowView())
                .setBackgroundColour(ContextCompat.getColor(this, R.color.primary))
                .setPrimaryTextColour(ContextCompat.getColor(this, R.color.white))
                .setSecondaryTextColour(ContextCompat.getColor(this, R.color.primary_light))
                .setFocalColour(ContextCompat.getColor(this, R.color.primary_light))
                .setPrimaryText("Taking a dose ${++stepCount}/${introStepCount}")
                .setSecondaryText("Click here when you take a dose")
                .setPromptStateChangeListener { prompt, state ->
                    if ((state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED || state == MaterialTapTargetPrompt.STATE_NON_FOCAL_PRESSED) && promptList.size > promptIndex + 1)
                        promptList[++promptIndex].show()
                })

        sequence.add(MaterialTapTargetPrompt.Builder(this)
                .setTarget(drugCard.getDoseLaterView())
                .setBackgroundColour(ContextCompat.getColor(this, R.color.primary))
                .setPrimaryTextColour(ContextCompat.getColor(this, R.color.white))
                .setSecondaryTextColour(ContextCompat.getColor(this, R.color.primary_light))
                .setFocalColour(ContextCompat.getColor(this, R.color.primary_light))
                .setPrimaryText("Recording an earlier dose ${++stepCount}/${introStepCount}")
                .setSecondaryText("Click here when record a dose that you took earlier")
                .setPromptStateChangeListener { prompt, state ->
                    if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED && promptList.size > promptIndex + 1)
                        inDemoMode = false
                })

        sequence.setShowCompleteAction { inDemoMode = false }
        sequence.show()
    }



    private fun OldTapTargetThing() {
        val drugCard = getFirstDrugCard()
        if (drugCard != null) {
            val introStepCount = "5"
            var stepCount = 0

            TapTargetSequence(this).targets(
                    TapTarget.forView(drugCard.getDrugInfoView(),
                            "Introduction ${++stepCount}/${introStepCount}",
                            "For each drug you'll be shown the number of doses you've taken in 24 hours, and the time you can take your next dose")
                            .defaultConfig(),
                    TapTarget.forView(drugCard.getDoseNowView(),
                            "Taking a dose ${++stepCount}/${introStepCount}",
                            "Click here when you take a dose")
                            .defaultConfig(),
                    TapTarget.forView(drugCard.getDoseLaterView(),
                            "Recording an earlier dose ${++stepCount}/${introStepCount}",
                            "Add a dose you took recently")
                            .defaultConfig(),
                    TapTarget.forView(drugCard.getDoseHistory(),
                            "Dose History ${++stepCount}/${introStepCount}",
                            "View or change the doses of this drug")
                            .defaultConfig(),
                    TapTarget.forToolbarOverflow(main_toolbar,
                            "More ... ${++stepCount}/${introStepCount}",
                            "Add and update drugs or change settings")
                            .defaultConfig()
            ).start()
        }
    }

    private fun getFirstDrugCard() =
            rv_drugs.findViewHolderForLayoutPosition(0) as DrugCardAdapter.DrugHolder

    fun takeDose(drugId: Long, hourOfDay: Int, minute: Int) {
        if (inDemoMode) return
        viewModel.takeDose(drugId, hourOfDay, minute) // todo could this be behind an interface?
        showDoseTakenMessage(getDoseDescription(hourOfDay, minute))
    }

    private fun takeDoseNow(drug: Drug) {
        if (inDemoMode) return
        viewModel.takeDose(drug)
        showDoseTakenMessage(getDoseDescription())
    }

    private fun showDoseTakenMessage(doseDesc: String) {
        Snackbar.make(main_container, "Dose Taken: $doseDesc", 1500).show()
    }
}