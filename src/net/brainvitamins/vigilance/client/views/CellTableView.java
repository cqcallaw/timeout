package net.brainvitamins.vigilance.client.views;

import com.google.gwt.user.cellview.client.CellTable;

public interface CellTableView<T>
{
	public abstract CellTable<T> getCellView();
}