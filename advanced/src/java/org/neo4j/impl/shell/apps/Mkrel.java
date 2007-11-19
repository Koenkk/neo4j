/*
 * Copyright 2002-2007 Network Engine for Objects in Lund AB [neotechnology.com]
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.impl.shell.apps;

import org.neo4j.api.core.Direction;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.RelationshipType;
import org.neo4j.impl.core.NodeManager;
import org.neo4j.impl.shell.NeoApp;
import org.neo4j.util.shell.AppCommandParser;
import org.neo4j.util.shell.OptionValueType;
import org.neo4j.util.shell.Output;
import org.neo4j.util.shell.Session;
import org.neo4j.util.shell.ShellException;

public class Mkrel extends NeoApp
{
	public Mkrel()
	{
		this.addValueType( "t", new OptionContext( OptionValueType.MUST,
			"The relationship type" ) );
		this.addValueType( "n", new OptionContext( OptionValueType.MUST,
			"The node id to connect to" ) );
		this.addValueType( "d", new OptionContext( OptionValueType.MUST,
			"The direction: " + this.directionAlternatives() + "." ) );
		this.addValueType( "c", new OptionContext( OptionValueType.NONE,
			"Supplied if there should be created a new node" ) );
	}

	@Override
	public String getDescription()
	{
		return "Creates a relationship to a node";
	}

	@Override
	protected String exec( AppCommandParser parser, Session session, Output out )
		throws ShellException
	{
		boolean createNode = parser.options().containsKey( "c" );
		boolean suppliedNode = parser.options().containsKey( "n" );
		Node node = null;
		if ( createNode )
		{
			node = NodeManager.getManager().createNode();
		}
		else if ( suppliedNode )
		{
			node = getNodeById( Long.parseLong( parser.options().get( "n" ) ) );
		}
		else
		{
			throw new ShellException( "Must either create node (-c)" +
				" or supply node id (-n <id>)" );
		}
		
		if ( parser.options().get( "t" ) == null )
		{
			throw new ShellException( "Must supply relationship type " +
				"(-t <relationship-type-name>)" );
		}
		RelationshipType type = this.getRelationshipType(
			parser.options().get( "t" ) );
		Direction direction = this.getDirection( parser.options().get( "d" ) );
		Node startNode = direction == Direction.OUTGOING ?
			this.getCurrentNode( session ) : node;
		Node endNode = direction == Direction.OUTGOING ?
			node : this.getCurrentNode( session );
		startNode.createRelationshipTo( endNode, type );
		return null;
	}
}
