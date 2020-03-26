package UI;

import java.awt.EventQueue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;

import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import DTO.Dungeon;

import javax.imageio.stream.ImageInputStream;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.awt.event.ActionEvent;



public class window {

	private JFrame frame;
	
	private JPanel rightPanel = new JPanel();
	private JPanel IaEquipmentPanel = new JPanel();
	private JPanel mainPanel = new JPanel();
	private JPanel leftPanel = new JPanel();
	private JPanel infosPanel = new JPanel();
	private JPanel centerPanel = new JPanel();
	private JPanel centerTopPanel = new JPanel();
	private JPanel centerBottomPanel = new JPanel();
	private JPanel legendPanel = new JPanel();
	private JPanel pickupPanel = new JPanel();
	private JPanel pageSelectorPanel = new JPanel();

	private JButton btnMenuTerrain = new JButton("Terrains");
	private JButton btnMenuMonsters = new JButton("Monsters");
	private JButton btnMenuOthers = new JButton("Others");
	private JButton previousPageBtn = new JButton("<");
	private JButton nextPageBtn = new JButton(">");

	private ArrayList<JButton> dungeonMapButtons = new ArrayList<>();
	private ArrayList<JButton> toolBoxButtons = new ArrayList<>();
	private ArrayList<JButton> iaEquipmentButtons = new ArrayList<>();
	private int mapWidth = 10, mapHeight = 10, toolBoxWidth = 4, toolBoxHeigth = 9;

	private JTextField dungeonNameTf;
	private JTextField pageIndex;
	private JTextField txtStuff;
	private JTextField monsterTxtValue;
	private JTextField terrainsTxtValue;
	private JTextField chestsTxtValue;
	private JTextField stuffsTxtValue;
	private JTextField lvlDiffTxtValue;
	
	//datas
	private int currentItemSelectedId = -1;
	private ArrayList<Integer> currentItemsIds = new ArrayList<>();
	private ArrayList<Integer> terrainIds = new ArrayList<>();
	private ArrayList<Integer> monsterIds = new ArrayList<>();
	private ArrayList<Integer> otherIds = new ArrayList<>();

	private ImageIcon imgNotFound = new ImageIcon("ressources/notFound.png");
	private ImageIcon currentIcon = imgNotFound;
	private ArrayList<ImageIcon> currentItemsIcon = new ArrayList<>();
	private ArrayList<ImageIcon> terrainIcon = new ArrayList<>();
	private ArrayList<ImageIcon> monsterIcon = new ArrayList<>();
	private ArrayList<ImageIcon> otherIcon = new ArrayList<>();
	
	
	
	
	//TODO temporary variables
	private int nbInputRessourcesTmp = 7;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					window window = new window();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public window() {
		initialize();
		loadDatas();
		installMap();
		installToolBox();
	}

	/**
	 * TODO Call to API for various datas
	 */
	private boolean loadDatas() {
		Map<String, String> parameters = new HashMap<>();
		try {
			fetchToApi("GET", "http://localhost:8080/ressources", parameters);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		for(int i = 0; i < nbInputRessourcesTmp; i++) {
			String path = "ressources/icon" + i + ".png";
			File f = new File(path);
			switch ((int) (Math.floor(Math.random() * 3))) {
			case 0:
				terrainIds.add(i);
				terrainIcon.add(f.exists() ? new ImageIcon(path) : imgNotFound);
				break;
			case 1:
				monsterIds.add(i);
				monsterIcon.add(f.exists() ? new ImageIcon(path) : imgNotFound);
				break;

			default:
				otherIds.add(i);
				otherIcon.add(f.exists() ? new ImageIcon(path) : imgNotFound);
				break;
			}
		}
		currentItemsIds = terrainIds;
		currentItemsIcon = terrainIcon;
		btnMenuTerrain.setEnabled(false);
		
		return true;
	}
	
	private void installMap() {
		for(int i = 0; i < mapWidth; i++) {
			for(int j = 0; j < mapHeight; j++) {
				ImageIcon img = new ImageIcon("ressources/icon0.png");
				JButton bt = new JButton("", img);
				bt.addActionListener(dungeonMapListener);
				bt.setActionCommand("0");
				dungeonMapButtons.add(bt);
				centerPanel.add(bt);
			}
		}
	}
	
	private void installToolBox() {
		int page = Integer.parseInt(pageIndex.getText()) -1;
		toolBoxButtons.removeAll(toolBoxButtons);
		pickupPanel.removeAll();
		for(int nb = page * toolBoxHeigth * toolBoxWidth; nb < (page +1) * toolBoxHeigth * toolBoxWidth; nb++) {
			JButton bt;
			if(nb >= currentItemsIds.size()) {
				bt = new JButton("");
				bt.setEnabled(false);
			}else {
				bt = new JButton("", currentItemsIcon.get(nb));
				bt.addActionListener(toolBoxListener);
				bt.setActionCommand(String.valueOf(currentItemsIds.get(nb)));
			}
			toolBoxButtons.add(bt);
			pickupPanel.add(bt);
		}
		pickupPanel.revalidate();
		pickupPanel.repaint();
	}
	
	/*
	 * Map<String, String> parameters = new HashMap<>() -> url parametres, ?limit=10&offset=10
	 */
	private void fetchToApi(String method, String inUrl, Map<String, String> parameters) throws Exception {
		//Create params string
		StringBuilder paramStringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
          paramStringBuilder.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
          paramStringBuilder.append("=");
          paramStringBuilder.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
          paramStringBuilder.append("&");
        }
        String paramString = paramStringBuilder.toString();
        paramString = paramString.length() > 0 ? paramString.substring(0, paramString.length() - 1) : paramString;
        
        //Create Request
		URL url = parameters.size() == 0 ? new URL(inUrl) : new URL(inUrl + "?" + paramString);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod(method);
		
        //Conf request
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:72.0) Gecko/20100101 Firefox/72.0");
		con.setConnectTimeout(5000);
		con.setReadTimeout(5000);
		
		
		
		//Read response
		int status = con.getResponseCode();
		InputStream bb = con.getInputStream();
		InputStreamReader aa = new InputStreamReader(bb);
		BufferedReader in = new BufferedReader(aa);
		String inputLine;
		StringBuffer content = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
		    content.append(inputLine);
		}
		in.close();
		con.disconnect();
		
		
		System.out.println(content.toString());

		//TODO Parse json
		
		
	}

	
	/*
	 * Map<String, String> parameters = new HashMap<>() -> url parametres, ?limit=10&offset=10
	 */
	private void fetchToApi(String method, String inUrl, Map<String, String> parameters, String jsonBodyString) throws Exception {
		//Create params string
		StringBuilder paramStringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
          paramStringBuilder.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
          paramStringBuilder.append("=");
          paramStringBuilder.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
          paramStringBuilder.append("&");
        }
        String paramString = paramStringBuilder.toString();
        paramString = paramString.length() > 0 ? paramString.substring(0, paramString.length() - 1) : paramString;
        
        //Create Request
		URL url = parameters.size() == 0 ? new URL(inUrl) : new URL(inUrl + "?" + paramString);
		HttpURLConnection con = (HttpURLConnection) (url).openConnection();
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setRequestMethod(method);
        con.setRequestProperty("Content-Type", "application/json");
        // linec.setRequestProperty("Authorization", "Bearer " + "1djCb/mXV+KtryMxr6i1bXw");

//        OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream(), "UTF-8");
//        writer.write("{\"userId\": 100,\"id\": 100,\"title\": \"main title\",\"body\": \"main body\"}");
        con.getOutputStream().write(jsonBodyString.getBytes());
		
		
        
        
        
        
        
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;

        while ((inputLine = in.readLine()) != null)
            System.out.println(inputLine);
        in.close();
        
        
        
        /*
		
		//Read response
		int status = con.getResponseCode();
		InputStream bb = con.getInputStream();
		InputStreamReader aa = new InputStreamReader(bb);
		BufferedReader in = new BufferedReader(aa);
		String inputLine;
		StringBuffer content = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
		    content.append(inputLine);
		}
		in.close();
		con.disconnect();
		
		
		System.out.println(content.toString());

		//TODO Parse json
		*/
		
	}
	
	private boolean saveDungeon() {
		//save map
		int[] dungeonCases = new int[mapHeight * mapWidth];
		int[][] dungeonCasesForCheck = new int[mapHeight][mapWidth];
		for (int i = 0; i < mapHeight; i++) {
			for (int j = 0; j < mapWidth; i++) {
				dungeonCases[i * j] = Integer.parseInt(dungeonMapButtons.get(i * j).getActionCommand());
				dungeonCasesForCheck[i][j] = Integer.parseInt(dungeonMapButtons.get(i * j).getActionCommand());
			}
		}
		
		int wallId = 1, entrenceId = 2, throneId = 3;
		if(!Dungeon.isCompletable(dungeonCasesForCheck, mapHeight, mapWidth, entrenceId, throneId, wallId))
			return false;
		
		//save equipment
		ArrayList<Integer> dungeonEquipmentArray = new ArrayList<Integer>();
		for(JButton bt : iaEquipmentButtons) {
			if(bt.isEnabled()){
				if(bt.getActionCommand() == "true") {
					dungeonEquipmentArray.add(iaEquipmentButtons.indexOf(bt));
				}
			}
		}
		if(dungeonEquipmentArray.size() == 0) {
			System.out.println("not enought equipment choosed");
			return false;
		}
		//convert array to int[]
		int[] dungeonEquipment = new int[dungeonEquipmentArray.size()];
		for(int i = 0; i < dungeonEquipmentArray.size(); i++) {
			dungeonEquipment[i] = dungeonEquipmentArray.get(i);
		}
		
		//dungeon name
		String dungeonName = dungeonNameTf.getText();
		
		//create Data Transfert Object
		Dungeon dungeon = new Dungeon(dungeonName, 0, mapHeight, Integer.parseInt(lvlDiffTxtValue.getText()), dungeonEquipment, dungeonCases);
		
		
		//transform to JSON
		try {
			ObjectMapper mapper = new ObjectMapper();
			String jsonString = mapper.writeValueAsString(dungeon);
			System.out.println(jsonString);
			
			

			try {
				fetchToApi("POST", "http://localhost:8080/dungeons/", new HashMap<>(), jsonString);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			
			
			
			
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return false;
		}
		
		
		
		
		return true;
	}
	

	

	
	

	ActionListener dungeonMapListener = new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
	    	if(currentItemSelectedId != -1) {
		        JButton clickedButton = dungeonMapButtons.get(dungeonMapButtons.indexOf(e.getSource()));
		        clickedButton.setActionCommand(String.valueOf(currentItemSelectedId));
		        clickedButton.setIcon(currentIcon);
	    	}
	    }
	};
	
	ActionListener toolBoxListener = new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
	        JButton clickedButton = toolBoxButtons.get(toolBoxButtons.indexOf(e.getSource()));
	        currentItemSelectedId = Integer.parseInt(clickedButton.getActionCommand());
	        currentIcon = (ImageIcon) clickedButton.getIcon();
	    }
	};

	ActionListener iaEquipmentListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
	        JButton clickedButton = iaEquipmentButtons.get(iaEquipmentButtons.indexOf(e.getSource()));
			if(clickedButton.getActionCommand() == "true") {
				clickedButton.setActionCommand("false");
				clickedButton.setBorder(new LineBorder(Color.gray));
			}else {
				clickedButton.setActionCommand("true");
				clickedButton.setBorder(new LineBorder(Color.red));
			}
		}
	};
	
	
	
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		// INIT Frame
		frame = new JFrame();
		frame.setBounds(100, 100, 1280, 720);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
		mainPanel.setLayout(null);
		
		leftPanel.setBounds(0, 0, 248, 681);
		mainPanel.add(leftPanel);
		leftPanel.setLayout(null);
		
		infosPanel.setBounds(1, 0, 245, 340);
		leftPanel.add(infosPanel);
		infosPanel.setLayout(null);
		
		JTextField monstersTxt = new JTextField();
		monstersTxt.setText("Monsters");
		monstersTxt.setEditable(false);
		monstersTxt.setColumns(10);
		monstersTxt.setBounds(0, 65, 100, 20);
		infosPanel.add(monstersTxt);
		
		JTextField terrainsTxt = new JTextField();
		terrainsTxt.setText("Terrains");
		terrainsTxt.setEditable(false);
		terrainsTxt.setColumns(10);
		terrainsTxt.setBounds(0, 90, 100, 20);
		infosPanel.add(terrainsTxt);
		
		JTextField chestsTxt = new JTextField();
		chestsTxt.setText("Chests");
		chestsTxt.setEditable(false);
		chestsTxt.setColumns(10);
		chestsTxt.setBounds(0, 115, 100, 20);
		infosPanel.add(chestsTxt);
		
		txtStuff = new JTextField();
		txtStuff.setText("Stuff");
		txtStuff.setEditable(false);
		txtStuff.setColumns(10);
		txtStuff.setBounds(0, 140, 100, 20);
		infosPanel.add(txtStuff);
		
		monsterTxtValue = new JTextField();
		monsterTxtValue.setEditable(false);
		monsterTxtValue.setColumns(10);
		monsterTxtValue.setBounds(105, 65, 100, 20);
		infosPanel.add(monsterTxtValue);
		
		terrainsTxtValue = new JTextField();
		terrainsTxtValue.setEditable(false);
		terrainsTxtValue.setColumns(10);
		terrainsTxtValue.setBounds(105, 90, 100, 20);
		infosPanel.add(terrainsTxtValue);
		
		chestsTxtValue = new JTextField();
		chestsTxtValue.setEditable(false);
		chestsTxtValue.setColumns(10);
		chestsTxtValue.setBounds(105, 115, 100, 20);
		infosPanel.add(chestsTxtValue);
		
		stuffsTxtValue = new JTextField();
		stuffsTxtValue.setEditable(false);
		stuffsTxtValue.setColumns(10);
		stuffsTxtValue.setBounds(105, 140, 100, 20);
		infosPanel.add(stuffsTxtValue);
		
		JTextField lvlDiffTxt = new JTextField();
		lvlDiffTxt.setText("Level Diff.");
		lvlDiffTxt.setEditable(false);
		lvlDiffTxt.setColumns(10);
		lvlDiffTxt.setBounds(0, 190, 100, 20);
		infosPanel.add(lvlDiffTxt);
		
		lvlDiffTxtValue = new JTextField("0");
		lvlDiffTxtValue.setEditable(false);
		lvlDiffTxtValue.setColumns(10);
		lvlDiffTxtValue.setBounds(105, 190, 100, 20);
		infosPanel.add(lvlDiffTxtValue);

		IaEquipmentPanel.setBounds(41, 340, 169, 283); //169-283
		leftPanel.add(IaEquipmentPanel);
		IaEquipmentPanel.setLayout(new GridLayout(5, 3, 2, 2));
		//TODO add equipments
		JButton btnEmpty = new JButton("");
		btnEmpty.setEnabled(false);

		JButton btn1 = new JButton("arc");
		iaEquipmentButtons.add(btn1);
		IaEquipmentPanel.add(btn1);
		JButton btn2 = new JButton("");
		btn2.setEnabled(false);
		iaEquipmentButtons.add(btn2);
		IaEquipmentPanel.add(btn2);
		JButton btn3 = new JButton("");
		btn3.setEnabled(false);
		iaEquipmentButtons.add(btn3);
		IaEquipmentPanel.add(btn3);
		JButton btn4 = new JButton("epee I");
		iaEquipmentButtons.add(btn4);
		IaEquipmentPanel.add(btn4);
		JButton btn5 = new JButton("epee II");
		iaEquipmentButtons.add(btn5);
		IaEquipmentPanel.add(btn5);
		JButton btn6 = new JButton("epee III");
		iaEquipmentButtons.add(btn6);
		IaEquipmentPanel.add(btn6);
		for(int i = 0; i < 9; i++) {
			JButton btn = new JButton("");
			btn.setEnabled(false);
			iaEquipmentButtons.add(btn);
			IaEquipmentPanel.add(btn);
		}
		for(JButton bt : iaEquipmentButtons) {
			bt.addActionListener(iaEquipmentListener);
			bt.setActionCommand("false");
			bt.setBorder(new LineBorder(Color.gray));
		}
		
		
		
		// Panel Center
		centerPanel.setBounds(344, 72, 550, 550);
		centerPanel.setLayout(new GridLayout(mapWidth, mapHeight, 0, 0));
		
		
		
		
		// Panel Center TOP
		centerTopPanel.setBounds(262, 0, 750, 60);
		mainPanel.add(centerTopPanel);
		centerTopPanel.setLayout(null);
		
		dungeonNameTf = new JTextField();
		dungeonNameTf.setText("Choose a name for your dungeon");
		dungeonNameTf.setBounds(0, 0, 750, 60);
		centerTopPanel.add(dungeonNameTf);
		dungeonNameTf.setColumns(10);
		
		
		mainPanel.add(centerPanel);
		
		
		
		// Panel Center Bottom
		centerBottomPanel.setBounds(262, 635, 750, 35);
		mainPanel.add(centerBottomPanel);
		centerBottomPanel.setLayout(null);
		
		JButton btnSaveLevel = new JButton("Save Level");
		btnSaveLevel.setBounds(325, 0, 100, 35);
		//TODO action performed
		btnSaveLevel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveDungeon();
			}
		});
		centerBottomPanel.add(btnSaveLevel);
		
		
		
		
		
		
		
		// Panel Right
		// Legend Panel
		rightPanel.setBounds(1028, 0, 235, 681);
		mainPanel.add(rightPanel);
		rightPanel.setLayout(null);
		
		legendPanel.setBounds(0, 29, 235, 30);
		rightPanel.add(legendPanel);
		legendPanel.setLayout(new GridLayout(1, 3, 2, 0));
		
		btnMenuTerrain.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentItemsIds = terrainIds;
				currentItemsIcon = terrainIcon;
				btnMenuTerrain.setEnabled(false);
				btnMenuMonsters.setEnabled(true);
				btnMenuOthers.setEnabled(true);
				pageIndex.setText("1");
				installToolBox();
			}
		});
		btnMenuTerrain.setBorder(new LineBorder(Color.gray));
		legendPanel.add(btnMenuTerrain);
		
		btnMenuMonsters.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentItemsIds = monsterIds;
				currentItemsIcon = monsterIcon;
				btnMenuTerrain.setEnabled(true);
				btnMenuMonsters.setEnabled(false);
				btnMenuOthers.setEnabled(true);
				pageIndex.setText("1");
				installToolBox();
			}
		});
		btnMenuMonsters.setBorder(new LineBorder(Color.gray));
		legendPanel.add(btnMenuMonsters);
		
		btnMenuOthers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentItemsIds = otherIds;
				currentItemsIcon = otherIcon;
				btnMenuTerrain.setEnabled(true);
				btnMenuMonsters.setEnabled(true);
				btnMenuOthers.setEnabled(false);
				pageIndex.setText("1");
				installToolBox();
			}
		});
		btnMenuOthers.setBorder(new LineBorder(Color.gray));
		legendPanel.add(btnMenuOthers);
		
		
		// Pickup panel
		pickupPanel.setBounds(6, 72, 226, 511);
		rightPanel.add(pickupPanel);
		pickupPanel.setLayout(new GridLayout(toolBoxHeigth, toolBoxWidth, 2, 2));
		
		
		
		
		// Page Selector Panel
		pageSelectorPanel.setBounds(1, 635, 235, 35);
		rightPanel.add(pageSelectorPanel);
		pageSelectorPanel.setLayout(null);
		
		previousPageBtn.setBounds(50, 0, 45, 35);
		previousPageBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int currentPageIndex = Integer.parseInt(pageIndex.getText()) -1;
				if(currentPageIndex -1 >= 0) {
					pageIndex.setText(String.valueOf(Integer.parseInt(pageIndex.getText()) -1));
					pageIndex.revalidate();
					pageIndex.repaint();
					installToolBox();
				}
			}
		});
		pageSelectorPanel.add(previousPageBtn);
		
		pageIndex = new JTextField();
		pageIndex.setText("1");
		pageIndex.setEditable(false);
		pageIndex.setBounds(100, 0, 45, 35);
		pageSelectorPanel.add(pageIndex);
		pageIndex.setColumns(10);
		
		nextPageBtn.setBounds(150, 0, 45, 35);
		nextPageBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int currentPageIndex = Integer.parseInt(pageIndex.getText()) -1;
				if(currentItemsIds.size() >= (currentPageIndex +1) * toolBoxHeigth * toolBoxWidth +1) {
					pageIndex.setText(String.valueOf(Integer.parseInt(pageIndex.getText()) +1));
					pageIndex.revalidate();
					pageIndex.repaint();
					installToolBox();
				}
			}
		});
		pageSelectorPanel.add(nextPageBtn);
	}
	

}
