package net.mcfr.tribe.resources.lists;

import net.mcfr.tribe.resources.VitalResourceDummy;

public class VitalResourceListDummy extends VitalResourceList{
	
	public VitalResourceListDummy () {
		super();
		this.add(new VitalResourceDummy());
		this.add(new VitalResourceDummy());
	}
}
