// PathVisio,
// a tool for data visualization and analysis using Biological Pathways
// Copyright 2006-2013 BiGCaT Bioinformatics
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package org.pathvisio.pluginmanager.impl.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.pathvisio.pluginmanager.impl.PluginManager;
import org.pathvisio.pluginmanager.impl.Utils;
import org.pathvisio.pluginmanager.impl.data.BundleVersion;
import org.pathvisio.pluginmanager.impl.data.Category;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * AvailablePluginsPanel.java
 * creates JPanel object shown in the
 * plugin manager dialog tab "Available"
 * 
 * @author martina
 *
 */
public class AvailablePluginsPanel extends JPanel {

	private PluginManager manager;
	private JTable available;
	private JPanel pluginInfo;
	private String currentTag = "all";
	private JComboBox tagBox;
	private JPanel pluginPanel;
	private JLabel numPlugins;
	private int countPlugins = 0;
	
	public AvailablePluginsPanel(PluginManager manager) {
		super();
		this.manager = manager;
		this.setBackground(Color.white);
		
		this.setLayout(new BorderLayout());
		pluginPanel = new JPanel();
		pluginPanel.setLayout(new GridLayout(1, 2));
		pluginPanel.setBackground(Color.white);
		
		List<BundleVersion> plugins = getAvailablePlugins();
		countPlugins = plugins.size();
		
		JPanel north = getNorthPanel();
		this.add(north, BorderLayout.NORTH);
		
		
		pluginPanel.add(new JScrollPane(getPluginsTable(plugins)));
		pluginInfo = new JPanel();
		pluginInfo.setBackground(Color.white);
		pluginPanel.add(pluginInfo);
		this.add(pluginPanel, BorderLayout.CENTER);
		
	}
	
	private JTable getPluginsTable(List<BundleVersion> plugins) {
		available = new JTable(new PluginTableModel(plugins));
		available.setBackground(Color.white);
		available.setSelectionForeground(Color.white);
		available.setSelectionBackground(Color.white);
		available.setDefaultRenderer(BundleVersion.class, new PluginCell(false, manager));
		available.setDefaultEditor(BundleVersion.class, new PluginCell(false, manager));
		available.setRowHeight(70);
		available.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int row = available.getSelectedRow();
				int column = available.getSelectedColumn();
				BundleVersion p = (BundleVersion) available.getValueAt(row, column);
				updatePluginDetails(p);
			}
		});
		return available;
	}
	
	protected void updatePluginDetails(BundleVersion p) {
		pluginInfo.removeAll();
		pluginInfo.setLayout(new GridLayout(1,1));
		pluginInfo.add(new JScrollPane(getPluginData(p).getPanel()));
		pluginInfo.revalidate();
		pluginInfo.repaint();
	}
	
	private PanelBuilder getPluginData(BundleVersion p) {
		FormLayout layout = new FormLayout("5dlu, fill:pref:grow, 5dlu","5dlu,pref,15dlu,pref,10dlu,pref,10dlu,pref,10dlu,pref,10dlu,pref,10dlu,pref,10dlu");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.setBackground(Color.white);
		
		builder.addLabel(p.getBundle().getName(), cc.xy(2, 2));
		builder.addSeparator("", cc.xyw(2, 3, 1));
		builder.addLabel((p.getBundle().getName().equals(p.getBundle().getSymbolicName()) ? "Version: " + p.getVersion() : ("<html>Version: " + p.getVersion() + "<br>" + p.getBundle().getSymbolicName() + "</html>")), cc.xy(2, 4));
		builder.addLabel((p.getBundle().getShortDescription() != null ? Utils.printDescription(p.getBundle().getShortDescription(), 40) : ""), cc.xy(2, 6));
		builder.addLabel((p.getReleaseDate() != null ? "Release date: " + p.getReleaseDate() : ""), cc.xy(2, 10));
	
		builder.addLabel(Utils.printAuthors(p), cc.xy(2, 12));
		builder.add(getWebsiteLabel(p), cc.xy(2, 14));
		
		return builder;
	}
	
	private JLabel getWebsiteLabel(final BundleVersion p) {
		if(p.getBundle().getWebsite() != null && !p.getBundle().getWebsite().equals("")) {
			JLabel label = new JLabel("<html>Visit the <a href=\"" + p.getBundle().getWebsite() + "\">website</a> for more information.</html>");
			label.addMouseListener(new MouseListener() {
				
				@Override
				public void mouseReleased(MouseEvent arg0) {}
				
				@Override
				public void mousePressed(MouseEvent arg0) {}
				
				@Override
				public void mouseExited(MouseEvent arg0) {}
				
				@Override
				public void mouseEntered(MouseEvent arg0) {}
				
				@Override
				public void mouseClicked(MouseEvent arg0) {
					try {
						Desktop.getDesktop().browse(new URI(p.getBundle().getWebsite()));
					} catch (Exception e) {
						new JOptionPane("Could not open website.", JOptionPane.ERROR_MESSAGE);
					}
				}
			});
			return label;
		}
		
		return new JLabel();
	}
	
	private JPanel getNorthPanel() {
		FormLayout layout = new FormLayout("5dlu,pref,5dlu,pref,fill:pref:grow","10dlu,pref,5dlu,pref,15dlu");
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setBackground(Color.white);
		CellConstraints cc = new CellConstraints();
		
		builder.addLabel("Browse by tag", cc.xy(2, 2));
		tagBox = new JComboBox(getTags());
		tagBox.setSelectedItem(currentTag);
		tagBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String item = (String) tagBox.getSelectedItem();
		        if (item != null) {
		        	currentTag = item;
		        	updatePluginPanel();
		        }
			}
		});
		
		builder.add(tagBox, cc.xy(4, 2));
		numPlugins = new JLabel();
		Font newLabelFont=new Font(numPlugins.getFont().getName(),Font.ITALIC,numPlugins.getFont().getSize());
		numPlugins.setFont(newLabelFont);
		numPlugins.setText(countPlugins + " plugins were found.");
		builder.add(numPlugins, cc.xyw(4, 4, 2));
		builder.addSeparator("", cc.xyw(2, 5, 4));
		
		return builder.getPanel();
	}

	private List<BundleVersion> getAvailablePlugins() {
		List<BundleVersion> plugins = new ArrayList<BundleVersion>();
		if(currentTag.equals("all")) {
			for(BundleVersion plugin : manager.getAvailablePlugins()) {
				if(!plugin.isInstalled()) {
					plugins.add(plugin);
				}
			}
		} else {
			for(BundleVersion plugin : manager.getBundlesPerTag(currentTag)) {
				if(!plugin.isInstalled()) {
					plugins.add(plugin);
				}
			}
		}
		Collections.sort(plugins, new Comparator<BundleVersion>() {

			@Override
			public int compare(BundleVersion o1, BundleVersion o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		return plugins;
	}
	
	public void updatePluginPanel() {
		pluginPanel.removeAll();
		List<BundleVersion> plugins = getAvailablePlugins();
		countPlugins = plugins.size();
		pluginPanel.add(new JScrollPane(getPluginsTable(plugins)));
		pluginInfo = new JPanel();
		pluginInfo.setBackground(Color.white);
		pluginPanel.add(pluginInfo);
		pluginPanel.revalidate();
		pluginPanel.repaint();
		numPlugins.removeAll();
		numPlugins.setText(countPlugins + " plugins were found.");
		numPlugins.revalidate();
		numPlugins.repaint();
	}
	
	public Vector<String> getTags() {
		Vector<String> tags = new Vector<String>();
		tags.add("all");
		for(Category cat : manager.getAvailableTags()) {
			tags.add(cat.getName());
		}
		Collections.sort(tags);
		return tags;
	}

	public void setCurrentTag(String currentTag) {
		this.currentTag = currentTag;
	}
}
