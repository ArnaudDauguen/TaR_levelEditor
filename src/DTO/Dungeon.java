package DTO;

import java.awt.List;
import java.util.ArrayList;
import java.util.Set;

public class Dungeon {
	
	private String name;
	private int creatorId;
	private boolean isResolved;
	private boolean isPublished;
	private int timeFailed;
	private int size;
	private int difficulty;
	private int[] paths;
	private int[] stuff;
	private int[] cases;
	
	
	public Dungeon(String name, int creatorId, int size, int difficulty, int[] stuff, int[] cases) {
		this.name = name;
		this.creatorId = creatorId;
		isResolved = false;
		isPublished = true;
		timeFailed = 0;
		this.size = size;
		this.difficulty = difficulty;
		paths = new int[0];
		this.stuff = stuff;
		this.cases = cases;
	}
	
	
	public static boolean isCompletable(int[][] map, int mapHeight, int mapWidth, int entrenceId, int throneId, int wallId) {
		ArrayList<Integer> checkedId = new ArrayList<Integer>();
		int throneCaseId = -1, entrenceCaseId = -1;
		// found entrence & throne caseId
		for(int x = 0; x < mapHeight; x++) {
			for(int y = 0; y < mapWidth; y++) {
				if(map[x][y] == entrenceId)
					entrenceCaseId = x * mapWidth + y;
				if(map[x][y] == throneId)
					throneCaseId = x * mapWidth + y;
			}
		}
		if(throneCaseId == -1 || entrenceCaseId == -1)
			return false;
		
		checkedId.add(entrenceCaseId);
		// found if there is a way to go from entrence to throne (like a wave)
		int insertedCasesPerLoop;
		do {
			insertedCasesPerLoop = 0;
			for(int caseId : checkedId) {
				for(int neighbourCaseId : getNeighbourCases(caseId, mapWidth, mapHeight)) {
					if(neighbourCaseId == throneCaseId)
						return true;
					if(map[(int) Math.floor(neighbourCaseId / mapWidth)][neighbourCaseId % mapWidth] != wallId)
						if(!checkedId.contains(neighbourCaseId))
							checkedId.add(neighbourCaseId);
				}
			}
		}while(insertedCasesPerLoop > 0);
		return false;
	}
	
	private static ArrayList<Integer> getNeighbourCases(int posCaseId, int mapWidth, int mapHeight) {
		ArrayList<Integer> positions = new ArrayList<>();

		if(posCaseId +1 < mapHeight * mapWidth && (posCaseId +1) % mapWidth != 0) 
			positions.add(posCaseId +1);
		if(posCaseId -1 >= 0 && (posCaseId -1) % mapWidth != mapWidth -1) 
			positions.add(posCaseId -1);
		if(posCaseId + mapWidth < mapHeight * mapHeight)
			positions.add(posCaseId + mapWidth);
		if(posCaseId - mapWidth >= 0)
			positions.add(posCaseId - mapWidth);
		
		return positions;
	}
	
	public String toString() {
		String rep = "name:" + name + ",\n";
		rep += "creatorId" + String.valueOf(creatorId) + ",\n";
		rep += "isResolved" + String.valueOf(isResolved) + ",\n";
		rep += "isPublished" + String.valueOf(isPublished) + ",\n";
		rep += "timeFailed" + String.valueOf(timeFailed) + ",\n";
		rep += "size" + String.valueOf(size) + ",\n";
		rep += "difficulty" + String.valueOf(difficulty) + ",\n";
		rep += "stuff [" + stuff.length + "]: ";
		for(int i : stuff) {
			rep += String.valueOf(i) + ", ";
		}
		rep += "\n";
		rep += "cases [" + cases.length + "]: ";
		for(int i : cases) {
			rep += String.valueOf(i) + ", ";
		}
		rep += "\n";
		
		
		return rep;
	}
	
	
	
	
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getCreatorId() {
		return creatorId;
	}
	public void setCreatorId(int creatorId) {
		this.creatorId = creatorId;
	}
	public boolean isResolved() {
		return isResolved;
	}
	public void setResolved(boolean isResolved) {
		this.isResolved = isResolved;
	}
	public boolean isPublished() {
		return isPublished;
	}
	public void setPublished(boolean isPublished) {
		this.isPublished = isPublished;
	}
	public int getTimeFailed() {
		return timeFailed;
	}
	public void setTimeFailed(int timeFailed) {
		this.timeFailed = timeFailed;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public int getDifficulty() {
		return difficulty;
	}
	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}
	public int[] getPaths() {
		return paths;
	}
	public void setPaths(int[] paths) {
		this.paths = paths;
	}
	public int[] getStuff() {
		return stuff;
	}
	public void setStuff(int[] stuff) {
		this.stuff = stuff;
	}
	public int[] getCases() {
		return cases;
	}
	public void setCases(int[] cases) {
		this.cases = cases;
	}
	
	
}
