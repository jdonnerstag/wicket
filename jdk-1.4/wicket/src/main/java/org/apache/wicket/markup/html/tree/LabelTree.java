/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.wicket.markup.html.tree;

import java.io.Serializable;

import javax.swing.tree.TreeModel;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Simple tree component that uses label to render tree node.
 * 
 * @author Matej Knopp
 */
public class LabelTree extends BaseTree
{
	private static final long serialVersionUID = 1L;

	/**
	 * Construct.
	 * 
	 * @param id
	 */
	public LabelTree(String id)
	{
		super(id);
	}

	/**
	 * Construct.
	 * 
	 * @param id
	 * @param model
	 *            model that provides the {@link TreeModel}
	 */
	public LabelTree(String id, IModel model)
	{
		super(id, model);
	}

	/**
	 * Construct.
	 * 
	 * @param id
	 * @param model
	 *            Tree model
	 */
	public LabelTree(String id, TreeModel model)
	{
		super(id, new Model((Serializable)model));
	}

	/**
	 * @see org.apache.wicket.markup.html.tree.BaseTree#newNodeComponent(java.lang.String,
	 *      org.apache.wicket.model.IModel)
	 */
	protected Component newNodeComponent(String id, IModel model)
	{
		return new LabelIconPanel(id, model, this);
	}

}