package net.mcfr.tribe.resources.lists;

import net.mcfr.tribe.resources.ConstructionResourceDummy;

public class ConstructionResourceListDummy extends ConstructionResourceList {
	
	public ConstructionResourceListDummy () {
		super();
		this.add(new ConstructionResourceDummy());
		this.add(new ConstructionResourceDummy());
	}
}
