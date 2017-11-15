package jira.tool.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.util.ArrayList;

public class IndexedFocusTraversalPolicy extends FocusTraversalPolicy 
{
	private ArrayList<Component> components = new ArrayList<Component>();
	
	public void addIndexedComponent(Component component) 
	{
        components.add(component);
    }
	
	@Override
	public Component getComponentAfter(Container aContainer, Component aComponent) 
	{
		int atIndex = components.indexOf(aComponent);
        int nextIndex = (atIndex + 1) % components.size();
        return components.get(nextIndex);
	}

	@Override
	public Component getComponentBefore(Container aContainer, Component aComponent) 
	{
		int atIndex = components.indexOf(aComponent);
        int nextIndex = (atIndex + components.size() - 1) % components.size();
        return components.get(nextIndex);
	}

	@Override
	public Component getDefaultComponent(Container aContainer) 
	{
		return components.get(0);
	}

	@Override
	public Component getFirstComponent(Container aContainer) 
	{
		return components.get(0);
	}

	@Override
	public Component getLastComponent(Container aContainer) 
	{
		return components.get(components.size() - 1);
	}	
}
