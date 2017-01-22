package net.mcfr.tribe.resources.lists;

import net.mcfr.tribe.resources.ConfortResourceN1Dummy;

public class ConfortResourceN1ListDummy extends ConfortResourceN1List {
	
	public ConfortResourceN1ListDummy() {
		super();
		this.add(new ConfortResourceN1Dummy());
		this.add(new ConfortResourceN1Dummy());
	}
}
