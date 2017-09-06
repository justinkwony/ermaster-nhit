package org.insightech.er.common.widgets;

import org.eclipse.jface.viewers.OwnerDrawLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TableItem;

public abstract class CenterImageLabelProvider extends OwnerDrawLabelProvider {

	@Override
	protected void measure(Event event, Object element) {
	}

	@Override
	protected void paint(Event event, Object element) {
		Image img = getImage(element);

		if (img != null) {
			Rectangle bounds = ((TableItem) event.item).getBounds(event.index);
			Rectangle imgBounds = img.getBounds();
			bounds.width /= 2;
			bounds.width -= imgBounds.width / 2;
			bounds.height /= 2;
			bounds.height -= imgBounds.height / 2;

			int x = bounds.width > 0 ? bounds.x + bounds.width : bounds.x;
			int y = bounds.height > 0 ? bounds.y + bounds.height : bounds.y;

			event.gc.drawImage(img, x, y);
		}
	}

	protected abstract Image getImage(Object element);
}
