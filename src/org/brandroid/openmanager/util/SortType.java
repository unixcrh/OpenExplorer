package org.brandroid.openmanager.util;

import org.brandroid.openmanager.util.SortType.Type;

public class SortType {
	
	Type mWhich = Type.NONE;
	boolean mFoldersFirst = true;
	boolean mShowHiddenFiles = false;
	
	public SortType(Type which)
	{
		mWhich = which;
	}
	public SortType(String s)
	{
		String t = s;
		if(s.indexOf(" ") > -1)
			t = s.substring(0, s.indexOf(" ")).trim();
		for(Type type : SortType.Type.values())
			if(type.toString().equals(t))
				mWhich = type;
		if(s.indexOf("FM") > -1)
			mFoldersFirst = false;
		if(s.indexOf("SHOW") > -1)
			mShowHiddenFiles = true;
	}
	
	public boolean showHidden() { return mShowHiddenFiles; }
	public boolean foldersFirst() { return mFoldersFirst; }
	public Type getType() { return mWhich; }
	
	@Override
	public String toString() {
		return getType().toString() + " (" + (mShowHiddenFiles ? "SHOW+" : "HIDE+") + (mFoldersFirst ? "FF" : "FM") + ")";
	}
	
	public enum Type
	{
		NONE,
		ALPHA,
		TYPE,
		SIZE,
		SIZE_DESC,
		DATE,
		DATE_DESC,
		ALPHA_DESC
	}
	
	public static SortType NONE = new SortType(Type.NONE),
			ALPHA = new SortType(Type.ALPHA),
			TYPE = new SortType(Type.TYPE),
			SIZE = new SortType(Type.SIZE),
			SIZE_DESC = new SortType(Type.SIZE_DESC),
			DATE = new SortType(Type.DATE),
			DATE_DESC = new SortType(Type.DATE_DESC),
			ALPHA_DESC = new SortType(Type.ALPHA_DESC);

	public SortType setShowHiddenFiles(boolean hidden) {
		mShowHiddenFiles = hidden;
		return this;
	}
	public SortType setFoldersFirst(boolean first) {
		mFoldersFirst = first;
		return this;
	}
	public void setType(Type which) {
		mWhich = which;
	}
}