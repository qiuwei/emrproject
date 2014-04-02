package wi.annotator;

import java.awt.*;

import javax.swing.*; 
import java.io.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;


import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.Vector;

public class EmrRelationAnnotator
{
	public static int wordSize = 16;
	public static int chooseSize = 18;
	
	
	
	private static void addOpenFileButtonListener(JButton buttonOpen,final JTextPane textPane,final JTextField inputFile,final JTable entityTable,final JTable relationTable){
	    buttonOpen.addActionListener(new ActionListener()
	    {
	    	public void actionPerformed(ActionEvent e)
	    	{
				
	    		JFileChooser j=new JFileChooser(GlobalCache.currentPath);//�ļ�ѡ����
	    		j.setFileFilter(new EmrFileFiller(".xml"));
	    	    if(j.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
	    		{	    	    	
	    	    	try{
	    	    		File f=j.getSelectedFile();
	    	    		GlobalCache.currentPath = f.getAbsolutePath();
	    	    		FileInputStream in2=new FileInputStream(f);
	    	    		BufferedReader br = new BufferedReader(new InputStreamReader(in2,"UTF-8"));
	    	    		StringBuffer sb = new StringBuffer();
	    	    		String line = null;
	    	    		while((line = br.readLine())!= null){
	    	    			sb.append(line+"\n");
	    	    		}
	    	    		textPane.setText(sb.toString());
	    	    		br.close();
	    	    		
	    	    		SimpleAttributeSet attr=new SimpleAttributeSet();
	    	    		StyleConstants.setForeground(attr,Color.black);
	    	    		StyleConstants.setFontSize(attr,wordSize);
	    	    		((DefaultStyledDocument) textPane.getDocument()).setCharacterAttributes(0,textPane.getText().length(),attr,false);
	    	    		
	    	    		inputFile.setText(f.getAbsolutePath());
	    	    		if(entityTable != null){
	    	    			clearTable(entityTable);
	    	    		}
	    	    		if(relationTable != null){
	    	    			clearTable(relationTable);
	    	    		}
	    	    	}catch(Exception ee){}
	    		}
	    	    textPane.setCaretPosition(0);
	    	       
	    	}
	    });
	    
	}
	
	private static void clearTable(JTable table){
		DefaultTableModel model = (DefaultTableModel)table.getModel();
		
		for(int row = model.getRowCount() - 1;row >=0; row --){
			model.removeRow(row);
		}
	}
	
	private static void addImportNEButtonListener(JButton buttonInNE,final JTextPane textPane,final JTable table){
	    buttonInNE.addActionListener(new ActionListener()
	    {
	    	public void actionPerformed(ActionEvent e)
	    	{
	    		
	    		JFileChooser j=new JFileChooser(GlobalCache.currentPath);//�ļ�ѡ����
	    		j.setFileFilter(new EmrFileFiller(".ent,.qst"));
	    	    if(j.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
	    	    	 try{
	    	    		 clearTable(table);
	 	    	    	File f=j.getSelectedFile();
	 	    	    	FileInputStream in=new FileInputStream(f);
	 	    	    	GlobalCache.currentPath = f.getAbsolutePath();
	    	    		BufferedReader br = new BufferedReader(new InputStreamReader(in,"UTF-8"));
	    	    		StringBuffer sb = new StringBuffer();
	    	    		String line = null;
	    	    		Entity sudo = new Entity();
	    	    		sudo.setStartPos(0);
	    	    		sudo.setEndPos(textPane.getText().length());
	    	    		clearEntityColor(textPane,sudo);
	    	    		DefaultTableModel model = (DefaultTableModel)table.getModel();
	    	    		ArrayList<Entity> ents = new ArrayList<Entity>(); 
	    	    		while((line = br.readLine())!= null){
	    	    			if(line.length() > 0){
	    	    				Entity ent = Entity.createBySaveStr(line);	    	    				
	    	    				ents.add(ent);	    	    				
	    	    			}
	    	    		}
	    	    		Collections.sort(ents);
	    	    		int rowno = 0;
	    	    		for(Entity ent : ents){
	    	    			rowno ++;
	    	    			TypeColor assertType = null;
	    	    			if(ent.getAssertType() != null){
	    	    				assertType = TypeColorMap.getType(ent.getAssertType());
	    	    			}
	    	    			
	    	    			Object[] rowData = new Object[]{rowno,ent.toAnnotation(),TypeColorMap.getType(ent.getEntityType()),assertType,ent.isQst()};
	    	    			model.addRow(rowData);
	    	    			setEntityForeground(textPane,ent,TypeColorMap.getType(ent.getEntityType()));
	    	    			
	    	    		}
	    	    		
	    	    		
	    	    		br.close();
	 	    	    	
	    	    	 }catch(Exception ex){
	    	    		 
	    	    	 }
	    	    }
	    	       
	    	}
	    });
	}
	private static void addImportNEForRelButtonListener(JButton buttonInNE,final JTextPane textPane,final JTable table){
		buttonInNE.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				
				JFileChooser j=new JFileChooser(GlobalCache.currentPath);//�ļ�ѡ����
				j.setFileFilter(new EmrFileFiller(".ent"));
				if(j.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
					try{
						clearTable(table);
						File f=j.getSelectedFile();
						FileInputStream in=new FileInputStream(f);
						GlobalCache.currentPath = f.getAbsolutePath();
						BufferedReader br = new BufferedReader(new InputStreamReader(in,"UTF-8"));
						String line = null;
						Entity sudo = new Entity();
						sudo.setStartPos(0);
						sudo.setEndPos(textPane.getText().length());
						clearEntityColor(textPane,sudo);
						DefaultTableModel model = (DefaultTableModel)table.getModel();
						ArrayList<Entity> ents = new ArrayList<Entity>(); 
						while((line = br.readLine())!= null){
							if(line.length() > 0){
								Entity ent = Entity.createBySaveStr(line);	    	    				
								ents.add(ent);	    	    				
							}
						}
						Collections.sort(ents);
						for(Entity ent : ents){
							TypeColor assertType = null;
							if(ent.getAssertType() != null){
								assertType = TypeColorMap.getType(ent.getAssertType());
							}
							
							Object[] rowData = new Object[]{ent.toAnnotation(),TypeColorMap.getType(ent.getEntityType()),assertType};
							model.addRow(rowData);
							setEntityForeground(textPane,ent,TypeColorMap.getType(ent.getEntityType()));
							
						}
						
						
						br.close();
						
					}catch(Exception ex){
						
					}
				}
				
			}
		});
	}
	
	private static void addAddNEButtonListener(JButton buttonNE,final JTextPane textPane,final JTable table){
//	    JButton buttonNE;//����ʵ�尴����ͨ���������ѡ������һ���֣�֮��������ʵ�弴��
//	    buttonNE = new JButton("����ʵ�� A");
	    buttonNE.setMnemonic(KeyEvent.VK_A);
	    buttonNE.addActionListener(new ActionListener()
	    {
	    	public void actionPerformed(ActionEvent e)
	    	{
	    		int p0 = textPane.getSelectionStart();//ѡ�����ݵ���ʼλ��
	    		int p1 = textPane.getSelectionEnd();//ѡ�����ݵ���ֹλ��

	    		hidePopupMenu();
	    	
	    		if (p0 < p1)
	    		{
			    	
			    	DefaultTableModel model = (DefaultTableModel)table.getModel();			    	
			    	try {
			    		Entity entity = new Entity();
			    		entity.setEntity(textPane.getText(p0, p1-p0));
			    		entity.setStartPos(p0);
			    		entity.setEndPos(p1);
			    		entity = EntityCleaner.cleanEntity(entity);
			    		String annotationStr = entity.toAnnotation();
			    		model.addRow(new Object[]{table.getRowCount()+1,annotationStr,null,null});
			    		int row = model.getRowCount() - 1;
			    		table.setRowSelectionInterval(row, row);
			    		
			    		setEntityBackground(textPane,entity);
			    		
			    		Rectangle rect = table.getCellRect(table.getRowCount()-1, 0, true);  
			    		table.scrollRectToVisible(rect);

			    		
					} catch (BadLocationException e1) {
						e1.printStackTrace();
					}
			    	table.repaint();
	    		}
	    		else
	    		{
	    			JOptionPane.showMessageDialog(null, "����ѡ��һ���Ϸ���ʵ��", "��ʾ",
	    		            JOptionPane.INFORMATION_MESSAGE);
	    		}
	    	}
	    });
	}
	
	
	private static void addDeleteNEButtonListener(JButton buttonNO,final JTextPane textPane,final JTable table){
//	    JButton buttonNO;//ɾ��һ��ʵ��
//	    buttonNO = new JButton("ɾ��ʵ�� D");
	    buttonNO.setMnemonic(KeyEvent.VK_D);
	    buttonNO.addActionListener(new ActionListener()
	    {
	    	public void actionPerformed(ActionEvent e)
	    	{
	    		hidePopupMenu();
	    		
	    		int response = JOptionPane.showConfirmDialog(null, "���Ƿ�ϣ�������ǰѡ��ʵ�壿", "��ʾ", JOptionPane.YES_NO_OPTION);
	    		
	    		if (response == 0)
	    		{
			    	int row = table.getSelectedRow();
			    	if (row >= 0)
			    	{
			    		String annoationStr = (String)table.getValueAt(row, table.getColumnModel().getColumnIndex("ʵ��"));
			    		Entity ent = Entity.createByAnnotationStr(annoationStr);
			    		clearEntityColor(textPane, ent);
			    		((DefaultTableModel)table.getModel()).removeRow(row);
			    	}
			    	else
			    	{
			    		JOptionPane.showMessageDialog(null, "����ѡ��һ���Ѿ���ע��ʵ��", "��ʾ",
			    		           JOptionPane.INFORMATION_MESSAGE);
			    	}
	    		}
	    	}
	    });
	}
	
	//E=�Թ��� P=p0:p1 T=���� A=��ǰ��
	private static void addExportNEButtonToTab1(JButton buttonSave,final JTable table,final JTextField inputFile){
	    buttonSave.addActionListener(new ActionListener()
	    {
	    	public void actionPerformed(ActionEvent e)
	    	{
	    		if(table.getRowCount() == 0){
	    			return;
	    		}
	    		DefaultTableModel model = (DefaultTableModel)table.getModel();
    			Vector rowdatas = model.getDataVector();
    			ArrayList<Entity> entities = new ArrayList<Entity>();
    			StringBuffer sb = new StringBuffer();
    			StringBuffer warning = new StringBuffer();
    			boolean existQst = false;
    			for(Object obj : rowdatas){
    				String outStr = "";
    				Vector rowdata = (Vector)obj;
    				String annotation = (String)rowdata.get(1);
    				Entity ent = Entity.createByAnnotationStr(annotation);
    				TypeColor entitytype = (TypeColor)rowdata.get(2);
    				boolean needAssert = false;
    				if(entitytype != null){
    					ent.setEntityType(entitytype.getTypeId());
    					if(entitytype.getTypeId().equals("disease") ||
    							entitytype.getTypeId().equals("complaintsymptom") ||
    							entitytype.getTypeId().equals("testresult") ||
    							entitytype.getTypeId().equals("treatment")){
    						needAssert = true;
    					}
    				}else{
    					int rowno = (Integer)rowdata.get(0);
    					sb.append("��"+rowno+"��ʵ��Ӧѡ��ʵ������\n");
    				}
    				
    				if(ent.getEntity().matches(".*\\d+.*")){
    					int rowno = (Integer)rowdata.get(0);
    					warning.append("��"+rowno+"��ʵ���а�������\n");
    				}
    				TypeColor asserttype = (TypeColor)rowdata.get(3);
    				if(asserttype != null){
    					ent.setAssertType(asserttype.getTypeId());
    				}else{
    					if(needAssert){
    						int rowno = (Integer)rowdata.get(0);
    						sb.append("��"+rowno+"����Ҫѡ����������\n");
    					}
    				}
    				
    				Boolean qst = (Boolean)rowdata.get(4);
    				if(qst != null && qst){
    					ent.setQst(qst);
    					existQst = true;
    				}
    				
    				if(ent.getEntityType() != null){
    					entities.add(ent);
    				}
    				
    			}
	    		
	    		String path;
	    		JFileChooser file = new JFileChooser (GlobalCache.currentPath);
//	    		file.setAcceptAllFileFilterUsed(false);
	    		File tobedeletedFile = null;
	    		if(existQst){
	    			file.setSelectedFile(new File(inputFile.getText()+".ent.qst"));
	    		}else{
	    			file.setSelectedFile(new File(inputFile.getText()+".ent"));
	    			tobedeletedFile = new File(inputFile.getText()+".ent.qst");
	    		}
	    		
	    		
	    		if(file.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
	    		{
	    			path = file.getSelectedFile().getAbsolutePath();
		    		try {
		    			GlobalCache.currentPath = path;	    			
		    			
		    			String errMsg = sb.toString();
		    			if(errMsg.length() == 0){
		    				String warningMsg = warning.toString();
		    				boolean resume = true;
		    				if(warningMsg.length() > 0){
		    					warningMsg = warningMsg +"\n �Ƿ������";
		    					int state = JOptionPane.showConfirmDialog(null, warningMsg, "����", JOptionPane.YES_NO_OPTION);
		    					if(state != JOptionPane.YES_OPTION){
		    						resume = false;
		    					}
		    				}
		    				if(resume){
		    					PrintWriter out = new PrintWriter(path,"UTF-8");
		    					Collections.sort(entities);
		    					for(Entity ent : entities){
		    						out.println(ent.toSave());
		    					}		    				
		    					out.flush();
		    					out.close();
		    					
		    					if(tobedeletedFile !=null && tobedeletedFile.exists()){
		    						tobedeletedFile.delete();
		    					}
		    					
		    					JOptionPane.showMessageDialog(null, "����ɹ�  ·����"+path, "��ʾ",JOptionPane.INFORMATION_MESSAGE);
		    				}else{
		    					JOptionPane.showMessageDialog(null, "��ע���δ����", "��ʾ",JOptionPane.INFORMATION_MESSAGE);
		    				}
		    				
		    			}else{
		    				JOptionPane.showMessageDialog(null,errMsg, "����",
		    						JOptionPane.INFORMATION_MESSAGE);
		    			}
		    			
		    			
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
		    		
		    		
	    		}
	    		else
	    		{
	    			JOptionPane.showMessageDialog(null, "��ȡ��  δ����", "��ʾ",
	    		            JOptionPane.INFORMATION_MESSAGE);
	    		}
	    	}
	    });
		
	}
	
	
	private static void addTableMouseListener(final JTextPane textPane,final JTable table){
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
//				int row = table.getSelectedRow();
//				String entityvalue = (String)table.getValueAt(row, table.getColumnModel().getColumnIndex("ʵ��"));
//				Entity ent = Entity.createByAnnotationStr(entityvalue);
//				setEntityBackground(textPane,ent);
//				GlobalCache.pastSelectedEntity = ent;
//				textPane.setCaretPosition(ent.getStartPos());
				setEntitySelected(textPane,table);
			}
		});
	}
	
	private static void setEntitySelected(JTextPane textPane,JTable table){
		int row = table.getSelectedRow();
		String entityvalue = (String)table.getValueAt(row, table.getColumnModel().getColumnIndex("ʵ��"));
		Entity ent = Entity.createByAnnotationStr(entityvalue);
		setEntityBackground(textPane,ent);
		GlobalCache.pastSelectedEntity = ent;
		textPane.setCaretPosition(ent.getStartPos());
	}
	
	private static void clearEntityColor(JTextPane textPane,Entity ent){
		int p0 = ent.getStartPos();
		int p1 = ent.getEndPos();
	    SimpleAttributeSet attr=new SimpleAttributeSet();
    	StyleConstants.setBackground(attr,Color.WHITE);
    	StyleConstants.setForeground(attr,Color.BLACK);
    	StyleConstants.setFontSize(attr,chooseSize);
    	((DefaultStyledDocument) textPane.getDocument()).setCharacterAttributes(p0,p1-p0,attr,false);
	}
	
	private static void setEntityForeground(JTextPane textPane,Entity ent,TypeColor tc){
		if(tc != null){
			int p0 = ent.getStartPos();
			int p1 = ent.getEndPos();
			SimpleAttributeSet attr=new SimpleAttributeSet();
			StyleConstants.setForeground(attr,tc.getColor());
			StyleConstants.setFontSize(attr,chooseSize);
			((DefaultStyledDocument) textPane.getDocument()).setCharacterAttributes(p0,p1-p0,attr,false);
		}
	}
	
	private static void setEntityBackground(JTextPane textPane,Entity ent){
		if(GlobalCache.pastSelectedEntity != null){
			setEntityBackgroundColor(textPane,GlobalCache.pastSelectedEntity,Color.WHITE);
		}
		setEntityBackgroundColor(textPane,ent,TypeColor.SelectedColor);
		GlobalCache.pastSelectedEntity = ent;
	}
	
	
	private static void setEntityBackgroundColor(JTextPane textPane,Entity ent,Color color){
		int p0 = ent.getStartPos();
		int p1 = ent.getEndPos();
	    SimpleAttributeSet attr=new SimpleAttributeSet();
    	StyleConstants.setBackground(attr,color);
    	StyleConstants.setFontSize(attr,chooseSize);
    	((DefaultStyledDocument) textPane.getDocument()).setCharacterAttributes(p0,p1-p0,attr,false);
	}
	
	
	
	private static JTable createEntityTable(final JTextPane textPane,final boolean isForEntity){
		Object columnNames[] = {"�к�","ʵ��", "����","����","��ȷ��"};//�����4������
//		final Object rowData[][] = new Object[maxNum][2];//���������е�Ԫ������
		final JTable table = new JTable(null, columnNames);//��������
		DefaultTableModel model = new DefaultTableModel(){
			public boolean isCellEditable(int row, int column)
            {
						if(!isForEntity){
							return false;
						}
						if(getColumnName(column).equals("����") &&
								(getValueAt(row, column - 1) != null && getValueAt(row, column - 1).toString().length() > 0)){
							
							return true;
						}
						if(getColumnName(column).equals("����")){
							TypeColor tc = ((TypeColor)getValueAt(row, column - 1));
							if(tc != null && (tc.getFlag() == 1)){
								return true;
							}
						}
						if(getColumnName(column).equals("��ȷ��")){
							return true;
						}
                       return false;//�����������༭
            }
			public void setValueAt(Object aValue, int row, int column) {
				super.setValueAt(aValue, row, column);
				if(column == table.getColumnModel().getColumnIndex("����")){
					TypeColor tc = (TypeColor)aValue;
					String entityvalue = (String)table.getValueAt(row, table.getColumnModel().getColumnIndex("ʵ��"));
					Entity ent = Entity.createByAnnotationStr(entityvalue);				
			    	setEntityForeground(textPane,ent,tc);
			    	
			    	if(tc.getFlag() == 0){
			    		table.setValueAt(null, row, table.getColumnModel().getColumnIndex("����"));
			    	}
			    	if(tc.getFlag() == 1){
			    		DefaultCellEditor editor = (DefaultCellEditor)table.getCellEditor(row, table.getColumnModel().getColumnIndex("����"));
			    		AssertTypeComboxModel model = (AssertTypeComboxModel)((JComboBox)editor.getComponent()).getModel();
			    		if(tc.getTypeId().equals("treatment")){
			    			model.setCondition("treatment");
			    		}else{
			    			model.setCondition("problem");
			    		}
			    	}

				}
			}
		};
		
		table.setSurrendersFocusOnKeystroke(true);
		model.setColumnIdentifiers(columnNames);
		table.setModel(model);
		
		
		table.setRowHeight(25);
		
		final JTextField inputFile = new JTextField(40);
		inputFile.setEditable(false);
		
		final JComboBox combo = new JComboBox();//����ʵ����������˵�
		for(TypeColor tc : TypeColorMap.getEntityTypeArray()){
			combo.addItem(tc);
		}
		combo.setRenderer(new ComboxRender(true));
	    combo.setEditable(false);
	    
	    
	    DefaultCellEditor typeeditor = new DefaultCellEditor(combo);
	    table.getColumn("����").setCellEditor(typeeditor);//����3����Ϊ��������ѡ��
	    table.getColumn("����").setCellRenderer(new TypeCellRender(true));
	    
	    AssertTypeComboxModel atcm = new AssertTypeComboxModel();
	    JComboBox combo2 = new JComboBox(atcm);//�������η��������˵�
	    AssertTypeMouseListener atml = new AssertTypeMouseListener(table,combo2);
	    combo2.getComponent(0).addMouseListener(atml);
//	    combo2.addFocusListener(atfl);
//		for(TypeColor tc : TypeColorMap.getAssertTypeArray()){
//			combo2.addItem(tc);
//		}
	    combo2.setEditable(false);
	    DefaultCellEditor asserteditor = new DefaultCellEditor(combo2);
	    table.getColumn("����").setCellEditor(asserteditor);//����3����Ϊ��������ѡ��
	    table.getColumn("����").setCellRenderer(new AsserttypeRender());
	    
	    table.getColumn("�к�").setPreferredWidth(1);
	    
	    table.getColumn("��ȷ��").setCellEditor(new DefaultCellEditor(new JCheckBox()));
	    addTableMouseListener(textPane,table);
	    table.getColumn("��ȷ��").setCellRenderer(new QuestionalRenderer());
	    
	    return  table;
	}
//	11
	private static JTable createEntityTableForRelation(final JTextPane textPane){
		Object columnNames[] = {"ʵ��", "����","����"};//�����4������
//		final Object rowData[][] = new Object[maxNum][2];//���������е�Ԫ������
		final JTable table = new JTable(null, columnNames);//��������
		DefaultTableModel model = new DefaultTableModel(){
			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
			public void setValueAt(Object aValue, int row, int column) {
				super.setValueAt(aValue, row, column);
				if(column == table.getColumnModel().getColumnIndex("����")){
					TypeColor tc = (TypeColor)aValue;
					String entityvalue = (String)table.getValueAt(row, table.getColumnModel().getColumnIndex("ʵ��"));
					Entity ent = Entity.createByAnnotationStr(entityvalue);				
					setEntityForeground(textPane,ent,tc);
					
					if(tc.getFlag() == 0){
						table.setValueAt(null, row, table.getColumnModel().getColumnIndex("����"));
					}
					if(tc.getFlag() == 1){
						DefaultCellEditor editor = (DefaultCellEditor)table.getCellEditor(row, table.getColumnModel().getColumnIndex("����"));
						AssertTypeComboxModel model = (AssertTypeComboxModel)((JComboBox)editor.getComponent()).getModel();
						if(tc.getTypeId().equals("treatment")){
							model.setCondition("treatment");
						}else{
							model.setCondition("problem");
						}
					}
					
				}
			}
		};
		
		table.setSurrendersFocusOnKeystroke(true);
		model.setColumnIdentifiers(columnNames);
		table.setModel(model);
		
		
		table.setRowHeight(25);
		
		
//		final JComboBox combo = new JComboBox();//����ʵ����������˵�
//		for(TypeColor tc : TypeColorMap.getEntityTypeArray()){
//			combo.addItem(tc);
//		}
//		combo.setRenderer(new ComboxRender(true));
//		combo.setEditable(false);
//		
//		
//		DefaultCellEditor typeeditor = new DefaultCellEditor(combo);
//		table.getColumn("����").setCellEditor(typeeditor);//����3����Ϊ��������ѡ��
		table.getColumn("����").setCellRenderer(new TypeCellRender(true));
		
//		AssertTypeComboxModel atcm = new AssertTypeComboxModel();
//		JComboBox combo2 = new JComboBox(atcm);//�������η��������˵�
//		AssertTypeMouseListener atml = new AssertTypeMouseListener(table,combo2);
//		combo2.getComponent(0).addMouseListener(atml);
//	    combo2.addFocusListener(atfl);
//		for(TypeColor tc : TypeColorMap.getAssertTypeArray()){
//			combo2.addItem(tc);
//		}
//		combo2.setEditable(false);
//		DefaultCellEditor asserteditor = new DefaultCellEditor(combo2);
//		table.getColumn("����").setCellEditor(asserteditor);//����3����Ϊ��������ѡ��
		table.getColumn("����").setCellRenderer(new AsserttypeRender());
		
//		table.getColumn("�к�").setPreferredWidth(1);
		
//		table.getColumn("��ȷ��").setCellEditor(new DefaultCellEditor(new JCheckBox()));
		addTableMouseListener(textPane,table);
//		table.getColumn("��ȷ��").setCellRenderer(new QuestionalRenderer());
		
		return  table;
	}
	
	
	private static class QuestionalRenderer  extends DefaultTableCellRenderer{

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			// TODO Auto-generated method stub
			Boolean bjvalue = (Boolean)table.getValueAt(row, table.getColumnModel().getColumnIndex("��ȷ��"));
			JCheckBox jcb = new JCheckBox();
			if(bjvalue != null && bjvalue){
				jcb.setSelected(true);
				jcb.setBackground(Color.YELLOW);
			}else{
				jcb.setSelected(false);
			}
			return  jcb;				
		}
	}
	
	private static JPanel createEntityButtonPanel(JTextPane textPane,JTable table){
		JPanel btnpanel = new JPanel();
	    
	    JTextField inputFile = new JTextField(40);
		inputFile.setEditable(false);

	    JButton buttonOpen = new JButton("���ļ�");
	    JButton buttonInNE = new JButton("����ʵ��");
	    JButton buttonNE = new JButton("����ʵ�� A");
	    JButton buttonNO = new JButton("ɾ��ʵ�� D");
	    JButton buttonSave = new JButton("�������");
	    JPopupMenu popmenu = new JPopupMenu();
	    
	    btnpanel.add(buttonOpen);
	    btnpanel.add(buttonInNE);
	    btnpanel.add(buttonNE);
	    btnpanel.add(buttonNO);
	    btnpanel.add(inputFile);
	    btnpanel.add(buttonSave);
	    
	    GlobalComponent.addNEButton = new JButton("����ʵ�� A");
	    GlobalComponent.delNEButton = new JButton("ɾ��ʵ�� D");
	    
	    
	    addOpenFileButtonListener(buttonOpen,textPane,inputFile,table,null);
	    addAddNEButtonListener(buttonNE,textPane,table);
	    addExportNEButtonToTab1(buttonSave,table,inputFile);
	    addImportNEButtonListener(buttonInNE,textPane,table);
	    addDeleteNEButtonListener(buttonNO,textPane,table);
//	    addPopupMenuListener();
	    
	    copyListener(buttonNE,GlobalComponent.addNEButton);
	    copyListener(buttonNO,GlobalComponent.delNEButton);
	    
	    GlobalComponent.entiyPopupmenu = popmenu;

	    
	    return btnpanel;
	}
	
	
	private static void copyListener(JButton from, JButton to){
		ActionListener[] als = from.getActionListeners();
		for(ActionListener al : als){
			to.addActionListener(al);
		}
	}
	
	
	private static void addEntityAnnotationTab(JTabbedPane tabbedPane, String text)//ҳ��һ��ʵ���ע
	{
		JPanel entityPanel = new JPanel();//�½�һ������
		entityPanel.setLayout(new BorderLayout());//��BorderLayout�԰�����в���
		
		final JTextPane entityTextPane = new JTextPane();//�½�һ���ı��༭��������ʾ�ı������в���
		entityTextPane.setEditable(false);//���ı��ǲ������û��ڿ��ڱ༭��
	    
		final JTable table = createEntityTable(entityTextPane,true);	  
		addTextPaneListener(entityTextPane,table);
		
	    JPanel btnpanel = createEntityButtonPanel(entityTextPane,table);
	    entityPanel.add(btnpanel,BorderLayout.NORTH);
	   
	    
	    JSplitPane jSplitPane0 = new JSplitPane();
	    jSplitPane0.setSize(1024, 768);
	    jSplitPane0.setOrientation(JSplitPane.VERTICAL_SPLIT);
	    jSplitPane0.setDividerLocation(0.5);
	    entityPanel.add(jSplitPane0,BorderLayout.CENTER);
	    jSplitPane0.setTopComponent(new JScrollPane(entityTextPane));
	    
	    
	    jSplitPane0.setBottomComponent(new JScrollPane(table));
	    
		tabbedPane.addTab(text, entityPanel);
	}
	
	
	private static void addTextPaneListener(final JTextPane entityTextPane,final JTable table){
		MouseListener listenser = new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON3){
					boolean existed = false;
					int pos = entityTextPane.viewToModel(e.getPoint());
					int rows = table.getRowCount();
					for(int i = 0;i < rows;i ++){
						String entityvalue = (String)table.getValueAt(i, table.getColumnModel().getColumnIndex("ʵ��"));
						Entity ent = Entity.createByAnnotationStr(entityvalue);
						if(ent.getStartPos() <= pos && ent.getEndPos() >= pos){
							existed = true;
							table.setRowSelectionInterval(i, i);
							setEntitySelected(entityTextPane,table);
							Rectangle rect = table.getCellRect(i, 0, true);  
				    		table.scrollRectToVisible(rect);
							break;
						}
					}
					String selectedText = entityTextPane.getSelectedText();
					if(existed || (!existed && selectedText != null && selectedText.length() > 0)){
						showMenu(existed).show(entityTextPane, (int)e.getPoint().getX(), (int)e.getPoint().getY());
					}
				}
			}
			
		};
		
		entityTextPane.addMouseListener(listenser);
	}
	
	static void createRelPopMenu(final JTable table){
		JPopupMenu menu = new JPopupMenu();
		final JButton btn1 = new JButton("����ʵ�壨�飩1");
		final JButton btn2 = new JButton("����ʵ�壨�飩2");
		menu.add(btn1);
		menu.add(btn2);
		
		ActionListener al = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int i = table.getSelectedRow();
				String entityvalue = (String)table.getValueAt(i, table.getColumnModel().getColumnIndex("ʵ��"));
				String entityType = ((TypeColor)table.getValueAt(i, table.getColumnModel().getColumnIndex("����"))).getTypeId();
				Entity ent = Entity.createByAnnotationStr(entityvalue);
				ent.setEntityType(entityType);
				int addResult = 2;
				if(e.getSource() == btn1){
					addResult = addEntToList(GlobalComponent.entList1,ent,GlobalComponent.entityTxt1);					
				}else if(e.getSource() == btn2){
					addResult = addEntToList(GlobalComponent.entList2,ent,GlobalComponent.entityTxt2);
				}
				
				if(GlobalComponent.relationPopupmenu.isVisible()){
					GlobalComponent.relationPopupmenu.setVisible(false);
				}
				
				if(addResult == 2){
					JOptionPane.showMessageDialog(null,"ʵ�����Ͳ�һ��", "����",JOptionPane.INFORMATION_MESSAGE);
				}else if(addResult == 1){
					JOptionPane.showMessageDialog(null,"��ʵ���Ѽ���", "����",JOptionPane.INFORMATION_MESSAGE);
				}
			}
		};
		btn1.addActionListener(al);
		btn2.addActionListener(al);
		
		GlobalComponent.relationPopupmenu = menu;
		
	}
	
	private static int addEntToList( ArrayList<Entity> entList, Entity ent,JTextField tf){
		int canAdd = 2;
		if(entList.size() > 0){
			if(entList.get(0).getEntityType().equals(ent.getEntityType())){
				canAdd = 0;
				for(Entity e : entList){//�ж��Ƿ�����ͬ�ļ�����
					if(e.getStartPos() == ent.getStartPos() && e.getEndPos() == ent.getEndPos()){
						canAdd = 1;
						break;
					}
				}
			}else{
				canAdd = 2;
			}
		}else{
			canAdd = 0;
		}
		if(canAdd == 0){
			entList.add(ent);
			tf.setText(tf.getText()+ent.getEntity() + "�� ");
		}
		
		return canAdd;
	}
	
	private static void addRelTextPaneListener(final JTextPane entityTextPane,final JTable table){
		MouseListener listenser = new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON3){
					boolean existed = false;
					int pos = entityTextPane.viewToModel(e.getPoint());
					int rows = table.getRowCount();
					for(int i = 0;i < rows;i ++){
						String entityvalue = (String)table.getValueAt(i, table.getColumnModel().getColumnIndex("ʵ��"));
						Entity ent = Entity.createByAnnotationStr(entityvalue);
						if(ent.getStartPos() <= pos && ent.getEndPos() >= pos){
							existed = true;
							table.setRowSelectionInterval(i, i);
							setEntitySelected(entityTextPane,table);
							Rectangle rect = table.getCellRect(i, 0, true);  
							table.scrollRectToVisible(rect);
							break;
						}
					}
					String selectedText = entityTextPane.getSelectedText();
					if(existed || (!existed && selectedText != null && selectedText.length() > 0)){
						GlobalComponent.relationPopupmenu.show(entityTextPane, (int)e.getPoint().getX(), (int)e.getPoint().getY());
					}
				}
			}
			
		};
		
		entityTextPane.addMouseListener(listenser);
	}
	
	
	
	
	private static void hidePopupMenu(){
		if(GlobalComponent.entiyPopupmenu.isVisible()){
			GlobalComponent.entiyPopupmenu.setVisible(false);
		}
	}
	
	private static JPopupMenu showMenu(boolean existed){
		GlobalComponent.entiyPopupmenu.removeAll();
		if(existed){
			GlobalComponent.entiyPopupmenu.add(GlobalComponent.delNEButton);
		}else{
			GlobalComponent.entiyPopupmenu.add(GlobalComponent.addNEButton);
		}
		return GlobalComponent.entiyPopupmenu;
	}
	
	
	private static JTable createRelationTable(final JTextPane textPane ){
		
		Object columnNames[] = {"ʵ��(��)1", "ʵ��(��)2","��ϵ����"};//�����4������
//		final Object rowData[][] = new Object[maxNum][2];//���������е�Ԫ������
		final JTable table = new JTable(null, columnNames);//��������
		DefaultTableModel model = new DefaultTableModel(){
			public boolean isCellEditable(int row, int column)
			{
				if(getColumnName(column).equals("��ϵ����") ){
					return true;
				}

				return false;//�����������༭
			}
			public void setValueAt(Object aValue, int row, int column) {
				super.setValueAt(aValue, row, column);
				if(column == table.getColumnModel().getColumnIndex("��ϵ����")){
					String entity1value = (String)table.getValueAt(row, table.getColumnModel().getColumnIndex("ʵ��1"));
					Entity ent1 = Entity.createByAnnotationStr(entity1value);				
					String entity2value = (String)table.getValueAt(row, table.getColumnModel().getColumnIndex("ʵ��2"));
					Entity ent2 = Entity.createByAnnotationStr(entity2value);		
					TypeColor tc = (TypeColor)aValue;
					if(tc == null){
						setEntityPairBackGround(textPane,ent1,ent2,Color.WHITE);
						return;
					}
					
					if(tc.getFlag() == 0){
						JOptionPane.showMessageDialog(null, "��ѡ�����Ĺ�ϵ����", "��ʾ",
		    		            JOptionPane.INFORMATION_MESSAGE);
						setValueAt(null,row,column);
						setEntityPairBackGround(textPane,ent1,ent2,Color.WHITE);
					}else{
						setEntityPairBackGround(textPane,ent1,ent2,tc.getColor());
					}
					
			    	
				}
			}
		};
		
		table.setSurrendersFocusOnKeystroke(true);
		model.setColumnIdentifiers(columnNames);
		table.setModel(model);
		table.getColumn("��ϵ����").setCellRenderer(new TypeCellRender(false));
		
		table.setRowHeight(25);
		
		
		final JComboBox combo = new JComboBox();//����ʵ����������˵�
		TypeColor[] tcs = TypeColorMap.getRelationTypeArray();
		combo.setMaximumRowCount(tcs.length);
		combo.setRenderer(new ComboxRender(false));
		for(TypeColor tc : tcs){
			combo.addItem(tc);
		}
	    combo.setEditable(false);
	    
	    
	    DefaultCellEditor typeeditor = new DefaultCellEditor(combo);
	    table.getColumn("��ϵ����").setCellEditor(typeeditor);//����3����Ϊ��������ѡ��
	    
	    
	    addRelationTableMouseListener(textPane,table);
		
		
		return table;
	}
	
	
	private static void setEntityPairBackGround(JTextPane textPane,Entity ent1,Entity ent2,Color color){
		if(GlobalCache.pastSelectedEntityPair!= null){
			setEntityBackgroundColor(textPane,GlobalCache.pastSelectedEntityPair[0],Color.WHITE);
			setEntityBackgroundColor(textPane,GlobalCache.pastSelectedEntityPair[1],Color.WHITE);
		}
		setEntityBackgroundColor(textPane,ent1,color);
		setEntityBackgroundColor(textPane,ent2,color);
		
		GlobalCache.pastSelectedEntityPair = new Entity[]{ent1,ent2};
	}
	
	
	private static void addRelationTableMouseListener(final JTextPane textPane,final JTable table){
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				int row = table.getSelectedRow();
				String entity1value = (String)table.getValueAt(row, table.getColumnModel().getColumnIndex("ʵ��1"));
				Entity ent1 = Entity.createByAnnotationStr(entity1value);				
				String entity2value = (String)table.getValueAt(row, table.getColumnModel().getColumnIndex("ʵ��2"));
				Entity ent2 = Entity.createByAnnotationStr(entity2value);		
				//�ñ���ɫ
				TypeColor tc = (TypeColor)table.getValueAt(row, table.getColumnModel().getColumnIndex("��ϵ����"));
				if(tc != null){
					setEntityPairBackGround(textPane,ent1,ent2,tc.getColor());
				}else{
					setEntityPairBackGround(textPane,ent1,ent2,Color.WHITE);
				}

			}
		});
	}
	
	
	private static void addEntityBtn1Listener(JButton entityBtn1,final JTextField entityTxt1,final JTable entityTable){
		entityBtn1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row = entityTable.getSelectedRow();
				if(row >= 0){
					entityTxt1.setText((String)entityTable.getValueAt(row, entityTable.getColumnModel().getColumnIndex("ʵ��")));
				}else{
					JOptionPane.showMessageDialog(null, "����ѡ��ʵ����е�һ��", "��ʾ",
	    		            JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
	}
	
	private static void addAddrealtionBtnListener(JButton addRelationBtn,final JTextField entityTxt1,final JTextField entityTxt2,final JTable relationTable){
		addRelationBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(entityTxt1.getText().length() == 0 || entityTxt2.getText().length() == 0 
						|| entityTxt1.getText().equals(entityTxt2.getText())){
					JOptionPane.showMessageDialog(null, "ʵ��1��ʵ��2������Ϊ�ղ��Ҳ�����ͬ", "��ʾ",
							JOptionPane.INFORMATION_MESSAGE);
				}else{
					Object[] rowData = new Object[]{entityTxt1.getText(),entityTxt2.getText(),null};
					((DefaultTableModel)relationTable.getModel()).addRow(rowData);
					entityTxt1.setText("");
					entityTxt2.setText("");
				}
			}
		});
	}
	
	
	 private static void addDeleteRelBtnListener(JButton buttonNORel,final JTextPane entityTextPane,final JTable relationTable){
		 buttonNORel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int row = relationTable.getSelectedRow();
					String entity1value = (String)relationTable.getValueAt(row, relationTable.getColumnModel().getColumnIndex("ʵ��1"));
					Entity ent1 = Entity.createByAnnotationStr(entity1value);				
					String entity2value = (String)relationTable.getValueAt(row, relationTable.getColumnModel().getColumnIndex("ʵ��2"));
					Entity ent2 = Entity.createByAnnotationStr(entity2value);		
					setEntityPairBackGround(entityTextPane,ent1,ent2,Color.WHITE);
					((DefaultTableModel)relationTable.getModel()).removeRow(row);
				}
			});
	 }
	 
	private static void  addImportRelBtnListener(JButton buttonInRel,final JTable relationTable){
		 buttonInRel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
		    		JFileChooser j=new JFileChooser(GlobalCache.currentPath);//�ļ�ѡ����
		    		j.setFileFilter(new EmrFileFiller(".rel"));
		    	    if(j.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
		    	    	 try{
		 	    	    	File f=j.getSelectedFile();
		 	    	    	FileInputStream in=new FileInputStream(f);
		 	    	    	GlobalCache.currentPath = f.getAbsolutePath();
		    	    		BufferedReader br = new BufferedReader(new InputStreamReader(in,"UTF-8"));
		    	    		String line = null;
		    	    		DefaultTableModel model = (DefaultTableModel)relationTable.getModel();
		    	    		
		    	    		clearTable(relationTable);
		    	    		
		    	    		
		    	    		while((line = br.readLine())!= null){
		    	    			if(line.length() > 0){
		    	    				Relation rel = Relation.createBySaveStr(line);
//		    	    				Object[] rowData = new Object[]{rel.getEnt1().toAnnotation(),rel.getEnt2().toAnnotation(),TypeColorMap.getType(rel.getRelationType())};
//		    	    				model.addRow(rowData);
		    	    			}
		    	    		}
		    	    		br.close();
		    	    		
		 	    	    	
		    	    	 }catch(Exception ex){
		    	    		 
		    	    	 }
		    	    }
				}
			});
	 }
	 
	 private static void addSaveRelBtnLinstener(JButton buttonSave,final JTable relationTable,final JTextField inputFile){
		 buttonSave.addActionListener(new ActionListener()
		    {
		    	public void actionPerformed(ActionEvent e)
		    	{
		    		
		    		String path;
		    		JFileChooser file = new JFileChooser (GlobalCache.currentPath);
		    		file.setAcceptAllFileFilterUsed(false);
		    		file.setSelectedFile(new File(inputFile.getText()+".rel"));
		    		
		    		if(relationTable.getRowCount() == 0){
		    			return;
		    		}
		    		if(file.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
		    		{
		    			path = file.getSelectedFile().getAbsolutePath();
			    		try {
			    			GlobalCache.currentPath = path;
			    			PrintWriter out = new PrintWriter(path,"UTF-8");		
			    			DefaultTableModel model = (DefaultTableModel)relationTable.getModel();
			    			Vector rowdatas = model.getDataVector();
			    			for(Object obj : rowdatas){
			    				Vector rowdata = (Vector)obj;
			    				Entity ent1 = Entity.createByAnnotationStr((String)rowdata.get(0));
			    				Entity ent2 = Entity.createByAnnotationStr((String)rowdata.get(1));
			    				TypeColor relationtype = (TypeColor)rowdata.get(2);
			    				if(relationtype != null){
			    					Relation rel = new Relation();
//			    					rel.setEnt1(ent1);
//			    					rel.setEnt2(ent2);
			    					rel.setRelationType(relationtype.getTypeId());
			    					out.println(rel.toSave());
			    				}
			    			}
			    			
			    			out.flush();
			    			out.close();
			    			
			    			
						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
			    		
			    		JOptionPane.showMessageDialog(null, "����ɹ�  ·����"+path, "��ʾ",
		    		            JOptionPane.INFORMATION_MESSAGE);
		    		}
		    		else
		    		{
		    			JOptionPane.showMessageDialog(null, "��ȡ��  δ����", "��ʾ",
		    		            JOptionPane.INFORMATION_MESSAGE);
		    		}
		    	}
		    });
	 }
	
	private static JPanel createRelationButtonPanel(final JTextPane entityTextPane,final JTable entityTable,final JTable relationTable){
//		JPanel btnpanel = new JPanel(new GridLayout(0,1));
		
		JPanel btnpanel1 = new JPanel();
	    
	    JTextField inputFile = new JTextField(40);
		inputFile.setEditable(false);

	    JButton buttonOpen = new JButton("���ļ�"); 
	    JButton buttonInNE = new JButton("����ʵ��");
	    JButton buttonInRel = new JButton("�����ϵ");
	    JButton buttonSave = new JButton("�������");
	    btnpanel1.add(buttonOpen);
	    btnpanel1.add(inputFile);
	    btnpanel1.add(buttonInNE);
	    btnpanel1.add(buttonInRel);
	    btnpanel1.add(buttonSave);
	    
	    addOpenFileButtonListener(buttonOpen,entityTextPane,inputFile,entityTable,relationTable);
	    addImportNEForRelButtonListener(buttonInNE,entityTextPane,entityTable);
	    addImportRelBtnListener(buttonInRel,relationTable);
	    addSaveRelBtnLinstener(buttonSave,relationTable,inputFile);
	    
	    
		
		
		
	    

//		entityBtnPanel.add(panel1);
//		entityBtnPanel.add(panel2);
//		entityBtnPanel.add(addRelationBtn);
//		entityBtnPanel.add(buttonNORel);
	    
	    
//		btnpanel.add(btnpanel1);
		
	    return btnpanel1;
		
		
		
	}
	
	private static void addButtonClearListener(JButton btn){
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ressetCurrentSelecttion();
			}
		});
		
	}
	
	private static void ressetCurrentSelecttion(){
		GlobalComponent.entityTxt1.setText("");
		GlobalComponent.entityTxt2.setText("");
		GlobalComponent.entList1.clear();
		GlobalComponent.entList2.clear();
	}
	
	static  JPanel createEntRelBtnPanel(final JTextPane entityTextPane,final JTable entityTable,final JTable relationTable){
		JPanel panel1 = new JPanel();
		panel1.setLayout(new BorderLayout());
		JLabel entityBtn1 = new JLabel("ʵ�壨�飩1");
		JTextField entityTxt1 = new JTextField();
		entityTxt1.setEditable(false);
		panel1.add(entityBtn1,BorderLayout.WEST);
		panel1.add(entityTxt1,BorderLayout.CENTER);		
		JPanel panel2 = new JPanel();
		panel2.setLayout(new BorderLayout());
		JLabel entityBtn2 = new JLabel("ʵ�壨�飩2");
		JTextField entityTxt2 = new JTextField();
		entityTxt2.setEditable(false);
		panel2.add(entityBtn2,BorderLayout.WEST);
		panel2.add(entityTxt2,BorderLayout.CENTER);
		
		GlobalComponent.entityTxt1 = entityTxt1;
		GlobalComponent.entityTxt2 = entityTxt2;
		
		
		
		JButton addRelationBtn = new JButton("����ʵ���ϵ");
	    JButton buttonNORel = new JButton("ɾ��ʵ���ϵ");
	    JPanel paneladddel = new JPanel();
	    paneladddel.setLayout(new BorderLayout());
	    paneladddel.add(addRelationBtn,BorderLayout.WEST);
	    paneladddel.add(buttonNORel,BorderLayout.EAST);
	    JButton buttonClear = new JButton("            �����ѡʵ�壨�飩             ");
	    addButtonClearListener(buttonClear);
	    
	    JPanel panel00 = new JPanel();
	    panel00.setLayout(new BorderLayout());
	    panel00.add(panel1,BorderLayout.CENTER);
	    panel00.add(buttonClear,BorderLayout.EAST);
	    
	    JPanel panel01 = new JPanel();
	    panel01.setLayout(new BorderLayout());
	    panel01.add(panel2,BorderLayout.CENTER);
	    panel01.add(paneladddel,BorderLayout.EAST);
	    
	    JPanel entityBtnPanel = new JPanel();
		entityBtnPanel.setLayout(new BorderLayout());
	    entityBtnPanel.add(panel00,BorderLayout.NORTH);
	    entityBtnPanel.add(panel01,BorderLayout.SOUTH);
	    
	    addAddrealtionBtnListener(addRelationBtn,entityTxt1,entityTxt2,relationTable);
	    addDeleteRelBtnListener(buttonNORel,entityTextPane,relationTable);
	    return entityBtnPanel;
	}
	
	
	
	
	static void addRelationAnnotaionTab(JTabbedPane tabbedPane, String text)//��ϵ��ע
	{
		JPanel relationPanel = new JPanel();
		relationPanel.setLayout(new BorderLayout());//��BorderLayout�԰�����в���
		
		final JTextPane entityTextPane = new JTextPane();
		entityTextPane.setEditable(false);

		
		JSplitPane entitySplitPane = new JSplitPane();
		entitySplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		entitySplitPane.setSize(1024, 384);
		entitySplitPane.setDividerLocation(0.3);
		final JTable entityTable = createEntityTableForRelation(entityTextPane);
		
		entitySplitPane.setLeftComponent(new JScrollPane(entityTable));
		entitySplitPane.setRightComponent(new JScrollPane(entityTextPane));
		
		createRelPopMenu(entityTable);
		addRelTextPaneListener(entityTextPane,entityTable);
		
	    JSplitPane mainSplitPane = new JSplitPane();
	    mainSplitPane.setSize(1024, 768);
	    mainSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
	    mainSplitPane.setDividerLocation(0.5);   
	    
	    mainSplitPane.setTopComponent(entitySplitPane);
	    
	    final JTable relationTable = createRelationTable(entityTextPane);	

	    JPanel btnpanel = createRelationButtonPanel(entityTextPane,entityTable,relationTable);
	    JPanel btnpanel2 = createEntRelBtnPanel(entityTextPane,entityTable,relationTable);
	    relationPanel.add(btnpanel,BorderLayout.NORTH);
	    
	    JPanel bottomPanel = new JPanel();
	    bottomPanel.setLayout(new BorderLayout());
	    bottomPanel.add(btnpanel2,BorderLayout.NORTH);
	    bottomPanel.add(new JScrollPane(relationTable),BorderLayout.CENTER);

	    mainSplitPane.setBottomComponent(bottomPanel);
	    relationPanel.add(mainSplitPane,BorderLayout.CENTER);
		
	    tabbedPane.addTab(text, relationPanel);
	    
	}
	
	

	public static void main(String args[])                                            //������
	{
		JFrame.setDefaultLookAndFeelDecorated(true);
		JFrame f = new JFrame("WIʵ���ҵ��Ӳ���ʵ���ʵ���ϵ��ע����1.0");
//		f.setResizable(false);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container content = f.getContentPane();
		JTabbedPane tabbedPane = new JTabbedPane();
		addEntityAnnotationTab(tabbedPane, "ʵ���ע");
		addRelationAnnotaionTab(tabbedPane, "ʵ���ϵ��ע");
		tabbedPane.setSelectedIndex(1);
		content.add(tabbedPane, BorderLayout.CENTER);
		f.setSize(1024, 768);
		f.setLocationRelativeTo(null);
		f.setVisible(true);
	}
}
