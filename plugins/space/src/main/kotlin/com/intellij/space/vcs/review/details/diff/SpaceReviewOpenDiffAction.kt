// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.space.vcs.review.details.diff

import com.intellij.diff.DiffDialogHints
import com.intellij.diff.DiffManager
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.AnActionExtensionProvider
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.space.vcs.review.SpaceReviewDataKeys
import com.intellij.space.vcs.review.details.CrDetailsVm
import libraries.coroutines.extra.LifetimeSource

class SpaceReviewOpenDiffActionProvider : AnActionExtensionProvider {
  override fun isActive(e: AnActionEvent): Boolean = getDetailsVm(e) != null

  override fun update(e: AnActionEvent) {
    val detailsVm = getDetailsVm(e) ?: return
    val isSelectionEmpty = detailsVm.changesVm.listSelection.value.isEmpty
    e.presentation.isEnabled = !isSelectionEmpty
  }

  override fun actionPerformed(e: AnActionEvent) {
    val project = e.getRequiredData(CommonDataKeys.PROJECT)
    val detailsVm = getDetailsVm(e) ?: return

    val chainBuilder = SpaceDiffRequestChainBuilder(LifetimeSource(),
                                                    project,
                                                    detailsVm.spaceDiffVm.value,
                                                    detailsVm.changesVm)
    val requestChain = chainBuilder.getRequestChain(detailsVm.changesVm.listSelection.value)
    DiffManager.getInstance().showDiff(project, requestChain, DiffDialogHints.DEFAULT)
  }

  private fun getDetailsVm(e: AnActionEvent): CrDetailsVm<*>? = e.getData(SpaceReviewDataKeys.REVIEW_DETAILS_VM)
}