package it.naturtalent.e4.project.search.textcomponents;



import it.naturtalent.e4.project.search.Ensure;

import java.util.Stack;

/**
 * @author Markus Gebhard
 */
public class ComponentPath
{
	private Stack/* <String> */componentStack = new Stack();

	public ComponentPath getClone()
	{
		ComponentPath clone = new ComponentPath();
		clone.componentStack = (Stack) componentStack.clone();
		return clone;
	}

	public boolean equals(Object obj)
	{
		if (!(obj instanceof ComponentPath))
		{
			return false;
		}
		ComponentPath other = (ComponentPath) obj;
		return componentStack.equals(other.componentStack);
	}

	public void push(String componentLabel)
	{
		Ensure.ensureArgumentNotNull(componentLabel);
		componentStack.push(componentLabel);
	}

	public void pop()
	{
		componentStack.pop();
	}

	public String[] getPathComponents()
	{
		return (String[]) componentStack.toArray(new String[componentStack
				.size()]);
	}

	public int length()
	{
		return componentStack.size();
	}

	public String peek()
	{
		return (String) componentStack.peek();
	}
}