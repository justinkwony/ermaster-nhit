package org.insightech.er.editor.view.dialog.common;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.ResourceString;

public class FileOverrideConfirmDialog extends MessageDialog {

	private static final String[] BUTTON_LABELS = { IDialogConstants.YES_LABEL,
			IDialogConstants.NO_LABEL, IDialogConstants.YES_TO_ALL_LABEL,
			IDialogConstants.NO_TO_ALL_LABEL, IDialogConstants.CANCEL_LABEL };

	public FileOverrideConfirmDialog(String filePath) {
		super(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				ResourceString.getResourceString("dialog.title.confirm"),
				null,
				"'"
						+ filePath
						+ "'"
						+ ResourceString
								.getResourceString("dialog.message.file.exist"),
				MessageDialog.CONFIRM, BUTTON_LABELS, 0);
	}

}
