/**
 * 20170903 Justin Kwon (justinkwony@gmail.com, younghkwon@nonghyup.com)
 */
package org.insightech.er.db.impl.cubrid;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.view.dialog.element.table_view.tab.AdvancedComposite;
import org.insightech.er.util.Format;

public class CUBRIDAdvancedComposite extends AdvancedComposite {

	private Button reuseOID;
	private Combo collationCombo;

	public CUBRIDAdvancedComposite(Composite parent) {
		super(parent);
	}

	@Override
	protected void initComposite() {
		super.initComposite();

		GridData gridData = new GridData();
		gridData.horizontalSpan = 2;

		this.reuseOID = new Button(this, SWT.CHECK);
		this.reuseOID.setText("Reuse OID");
		this.reuseOID.setLayoutData(gridData);
		
		this.collationCombo = CompositeFactory.createCombo(this.dialog, this, "label.collation", 1);
		this.collationCombo.setVisibleItemCount(20);
	}

	private void initCollationCombo() {
		this.collationCombo.add("");

		for (String collation : CUBRIDDBManager.getCollationList()) {
			this.collationCombo.add(collation);
		}
	}

	@Override
	protected void setData() {
		super.setData();

		this.reuseOID.setSelection(((CUBRIDTableProperties) this.tableViewProperties).isReuseOID());
		this.initCollationCombo();
		this.collationCombo.setText(Format.toString(((CUBRIDTableProperties) this.tableViewProperties).getCollation()));
	}

	/**
	 * {@inheritDoc}
	 * @return 
	 */
	@Override
	public boolean validate() throws InputException {
		super.validate();

		((CUBRIDTableProperties) this.tableViewProperties).setReuseOID(this.reuseOID.getSelection());
		((CUBRIDTableProperties) this.tableViewProperties).setCollation(this.collationCombo.getText());
		return super.validate();
	}
}
