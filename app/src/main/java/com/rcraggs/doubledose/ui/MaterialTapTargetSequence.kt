package com.rcraggs.doubledose.ui

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt

class MaterialTapTargetSequence {

    val prompts = ArrayList<MaterialTapTargetPrompt.Builder>()
    private var showIndex = 0
    private var finalCallback: (() -> Unit)? = null

    fun setShowCompleteAction(function: () -> Unit ){
        finalCallback = function
    }

    fun add(prompt: MaterialTapTargetPrompt.Builder){
        prompt.setPromptStateChangeListener { _, state ->
            if (state == MaterialTapTargetPrompt.STATE_NON_FOCAL_PRESSED ||
                    state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED)

                if (prompts.size > showIndex + 1) {
                    prompts[++showIndex].show()
                }else{
                    finalCallback
                }
        }
        prompts.add(prompt)
    }

    fun show() {
        if (prompts.isNotEmpty()){
            prompts[0].show()
        }
    }
}