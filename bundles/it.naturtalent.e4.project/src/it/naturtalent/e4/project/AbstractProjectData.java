package it.naturtalent.e4.project;

import java.beans.PropertyChangeEvent;

import org.apache.commons.lang3.StringUtils;

public abstract class AbstractProjectData extends BaseBean implements
		IProjectData, Cloneable
{
	// Properties der persistenten Daten
	public static final String PROP_ID = "id";
	public static final String PROP_NAME = "name";
	public static final String PROP_DESCRIPTION = "description";
		
	// persistente Daten
	protected String id;
	protected String name;
	protected String description;

	
	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		firePropertyChange(new PropertyChangeEvent(this, PROP_NAME, this.name,
				this.name = name));
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		firePropertyChange(new PropertyChangeEvent(this, PROP_DESCRIPTION, this.description,
				this.description = description));
	}
	
	@Override
	public Object clone()
	{		
		try
		{
			return super.clone();
		} catch (CloneNotSupportedException e)
		{			
		}
		return null;
	}
	
	public boolean compare(ProjectData projektData)
	{		
		if(!StringUtils.equals(id, projektData.id))
			return false;

		if(!StringUtils.equals(name, projektData.name))
			return false;

		if(!StringUtils.equals(description, projektData.description))
			return false;
		
		return true;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractProjectData other = (AbstractProjectData) obj;
		if (description == null)
		{
			if (other.description != null)
				return false;
		}
		else if (!description.equals(other.description))
			return false;
		if (id == null)
		{
			if (other.id != null)
				return false;
		}
		else if (!id.equals(other.id))
			return false;
		if (name == null)
		{
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		return true;
	}


}
