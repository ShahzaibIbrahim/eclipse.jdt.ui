package org.eclipse.jdt.internal.ui.util;

import org.eclipse.swt.graphics.Image;

import org.eclipse.jface.viewers.LabelProvider;

import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.JavaPluginImages;


public class TypeRefLabelProvider extends LabelProvider {
	
	public static final int SHOW_FULLYQUALIFIED= 0x01;
	public static final int SHOW_PACKAGE_POSTFIX= 0x02;
	
	public static final int SHOW_CONTAINER_ONLY= 0x04;
	
	private final Image CLASS_ICON= JavaPluginImages.get(JavaPluginImages.IMG_OBJS_CLASS);
	private final Image INTFC_ICON= JavaPluginImages.get(JavaPluginImages.IMG_OBJS_INTERFACE);
	private final Image PKG_ICON= JavaPluginImages.get(JavaPluginImages.IMG_OBJS_PACKAGE);
	
	private int fFlags;
	
	public TypeRefLabelProvider(int flags) {
		fFlags= flags;
	}	
	
	private boolean isSet(int flag) {
		return (fFlags & flag) != 0;
	}

	/**
	 * @see ILabelProvider#getText
	 */
	public String getText(Object element) {
		if (element instanceof TypeRef) {
			TypeRef typeRef= (TypeRef) element;
			StringBuffer buf= new StringBuffer();
			if (!isSet(SHOW_CONTAINER_ONLY)) {
				if (isSet(SHOW_FULLYQUALIFIED)) {
					buf.append(typeRef.getFullyQualifiedName());
				} else {
					buf.append(typeRef.getTypeQualifiedName());
				}
				if (isSet(SHOW_PACKAGE_POSTFIX)) {
					buf.append(" - ");
					String packName= typeRef.getPackageName();
					if (packName.length() == 0) {
						buf.append(JavaPlugin.getResourceString("DefaultPackage.label"));
					} else {
						buf.append(packName);
					}
				}
			} else {
				buf.append(typeRef.getTypeContainerName());
			}	
			return buf.toString();				
		}
		return super.getText(element);
					
	}
	
	/**
	 * @see ILabelProvider#getImage
	 */	
	public Image getImage(Object element) {
		if (element instanceof TypeRef) {
			TypeRef typeRef= (TypeRef) element;
			if (!isSet(SHOW_CONTAINER_ONLY)) {
				return typeRef.isInterface() ? INTFC_ICON : CLASS_ICON;
			} else {
				return (typeRef.getEnclosingName().length() > 0) ? PKG_ICON : CLASS_ICON;
			}
		}
		return super.getImage(element);	
	}	
	
}