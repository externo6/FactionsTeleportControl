package externo6.factionsteleportcontrol;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import externo6.factionsteleportcontrol.FactionsBridge.Bridge;
import externo6.factionsteleportcontrol.FactionsBridge.FactionsAny;
import externo6.factionsteleportcontrol.config.Config;
import externo6.factionsteleportcontrol.util.Q;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;


public abstract class Utilities {
	/* ********** FILE RELATED ********** */
	
	public static String readFileAsString(File filePath) {
		FileInputStream fstream=null;
		DataInputStream in=null;
		BufferedReader br=null;
		InputStreamReader isr = null;
		try {
			fstream = new FileInputStream(filePath);
			in = new DataInputStream(fstream);
			isr=new InputStreamReader(in);
			br = new BufferedReader(isr);
			String strLine;
			String fullThing = "";

			while ((strLine = br.readLine()) != null)   {
				fullThing = fullThing + strLine + "\r\n";
			}

//			in.close();

			return fullThing;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (null != br) {
				try {
					br.close();
				} catch ( IOException e ) {
					e.printStackTrace();
				}
			}
			
			if (null != isr) {
				try {
					isr.close();
				} catch ( IOException e ) {
					e.printStackTrace();
				}
			}
			
			if (null != in) {
				try {
					in.close();
				} catch ( IOException e ) {
					e.printStackTrace();
				}
			}
			
			if (null != fstream) {
				try {
					fstream.close();
				} catch ( IOException e ) {
					e.printStackTrace();
				}
			}
		}

		return null;
	}


	public static void writeToFile(String fileN, String T) {
		BufferedWriter bw =null;
		try {
			bw = new BufferedWriter(new FileWriter(new File(fileN), true));
			bw.write(T);
			bw.newLine();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (null != bw) {
				try {
					bw.close();
				} catch ( IOException e ) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static int countLines(String filename) throws IOException {
	    InputStream is = new BufferedInputStream(new FileInputStream(filename));
	    try {
	        byte[] c = new byte[1024];
	        int count = 0;
	        int readChars = 0;
	        boolean empty = true;
	        while ((readChars = is.read(c)) != -1) {
	            empty = false;
	            for (int i = 0; i < readChars; ++i) {
	                if (c[i] == '\n') {
	                    ++count;
	                }
	            }
	        }
	        return (count == 0 && !empty) ? 1 : count;
	    } finally {
	        is.close();
	    }
	}
	
	/* ********** FACTIONS RELATED ********** */

	public static boolean isOfficer(FPlayer fplayer) {
		return Bridge.factions.getRole( fplayer).equals( FactionsAny.Relation.OFFICER );
	}

	public static boolean isLeader(FPlayer fplayer) {
		return Bridge.factions.getRole( fplayer).equals( FactionsAny.Relation.LEADER );
	}

	
	/**
	 * @param fplayer
	 * @return null if offline
	 */
	public final static Player getOnlinePlayerExact(FPlayer fplayer) {
		if (null == fplayer) {
			return null;
		}
		return Utilities.getOnlinePlayerExact( fplayer.getId() );
	}
	
	/**
	 * @param playerName
	 * @return null if offline
	 */
	public final static Player getOnlinePlayerExact(String playerName) {
		if ((null == playerName) || (playerName.isEmpty())) {
			return null;
		}
		return Bukkit.getPlayerExact( playerName );
	}
	
//	public static boolean checkGroupPerm(World world, String group, String permission) {
//		if(Config.config.getBoolean("enablePermissionGroups")) {
//			return(FactionsPlus.permission.groupHas(world, group, permission));
//		} else {
//			return true;
//		}
//	}

	public static void addPower(Player player, double amount) {
		FPlayer fplayer = FPlayers.i.get(player);
		fplayer.setPowerBoost(fplayer.getPowerBoost() + amount);
	}

	public static void addPower(FPlayer player, double amount) {
		player.setPowerBoost(player.getPowerBoost() + amount);
	}

	public static void addPower(String player, double amount) {
		FPlayer fplayer = FPlayers.i.get(player);
		fplayer.setPowerBoost(fplayer.getPowerBoost() + amount);
	}

	public static void removePower(Player player, double amount) {
		FPlayer fplayer = FPlayers.i.get(player);
		fplayer.setPowerBoost(fplayer.getPowerBoost() - amount);
	}

	public static void removePower(FPlayer player, double amount) {
		player.setPowerBoost(player.getPowerBoost() - amount);
	}

	public static void removePower(String player, double amount) {
		FPlayer fplayer = FPlayers.i.get(player);
		fplayer.setPowerBoost(fplayer.getPowerBoost() - amount);
	}
	
	
	// ---------------- other simple utils
	
	public static File getCurrentFolder() {
		return new File(".");
	}
	
	/**
	 * the object is checked by reference (ie. == as opposed to .equals ) to see if it's contained in the array
	 * @param objRef
	 * @param array
	 * @return
	 */
	public final static boolean isReferenceInArray( Object objRef, Object[] array ) {
		for ( int i = 0; i < array.length; i++ ) {
			if (objRef == array[i]) { //not .equals(), we just want to know if that reference is in the array
				return true;
			}
		}
		return false;
	}
	
	/**
	 * uses .equals()
	 * @param objRef
	 * @param array
	 * @return
	 */
	public static int getIndexOfObjectInArray( Object objRef, Object[] array ) {
		for ( int i = 0; i < array.length; i++ ) {
			if (objRef.equals(array[i])) { //not .equals(), we just want to know if that reference is in the array
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * checks if player has permission<br />
	 * if he's OP then it auto has perm
	 * @param p
	 * @param perm
	 * @return
	 */
	public static boolean hasPermissionOrIsOp(Player p, Permission perm) {
		if (null != p) {
			return p.isOp() || p.hasPermission( perm );
		}
		return false;
	}
	
	public static boolean isOp(FPlayer fplayer) {
		return Utilities.getOnlinePlayerExact(fplayer).isOp();
	}
	
	public static boolean isWarZone(Faction faction)
	{
		return faction.getId().equals(ID_WARZONE);
	}

	public static boolean isSafeZone(Faction faction)
	{
		return faction.getId().equals(ID_SAFEZONE);
	}

	public static boolean isWilderness(Faction faction)
	{
		return faction.getId().equals(ID_WILDERNESS);
	}

	/**
	 * aka non-safezone and non-warzone and not wilderness
	 * @param faction
	 * @return true is so
	 */
	public static boolean isNormalFaction(Faction faction) {
		return !isWarZone( faction ) && !isSafeZone(faction) && !isWilderness( faction );
	}
	
	private static final int margin=10;//ie. 12.345 => 123 if margin is 10 or 1234 if margin is 100 ie. multiply by margin & truncate .*
	
	//XXX:don't change the following 3 constants:
	public static final String	ID_WARZONE	= "-2";
	public static final String	ID_SAFEZONE	= "-1";
	public  static final String	ID_WILDERNESS	= "0";
	
	public static boolean isJustLookingAround(Location from, Location to) {
		assert Q.nn( from );
		assert Q.nn( to );

	        if (from.getWorld() != to.getWorld() && (!from.getWorld().equals(to.getWorld()))) {
	            return false;
	        }
//	        System.out.println(getIntegerPartMultipzliedBy(from.getX(), 10)+" vs "+from.getX());
	        if (getIntegerPartMultipliedBy(from.getX(), margin) != getIntegerPartMultipliedBy(to.getX(), margin)) {
	            return false;
	        }
	        if (getIntegerPartMultipliedBy(from.getY(), margin) != getIntegerPartMultipliedBy(to.getY(), margin)) {
	            return false;
	        }
	        if (getIntegerPartMultipliedBy(from.getZ(), margin) != getIntegerPartMultipliedBy(to.getZ(), margin)) {
	            return false;
	        }
	        return true;
	}
	
	public static final int getIntegerPartMultipliedBy(double d, int multipliedByThis) {
		assert multipliedByThis>0;
		String asString = Double.toString( d*multipliedByThis );
		int dotAt = asString.indexOf( Config.DOT );
		if (dotAt<0) {//assumed it's fully integer
			dotAt=asString.length(); 
		}
//		multipliedByThis=Math.min( Math.getExponent( multipliedByThis )+dotAt, dotAt);
		return Integer.parseInt( asString.substring( 0, dotAt) );
	}
	
	/**
	 * just like: from=to; except the pitch/yal is not reset; ie. keep the pitch/yaw of "from"
	 * @param from
	 * @param to
	 * @return from  (unneeded but hey)
	 */
	public static final Location setLocationExceptEye(Location from, Location to) {
		assert Q.nn( from );
		assert Q.nn(to);
		from.setWorld( to.getWorld() );
		from.setX( to.getX() );
		from.setY( to.getY() );
		from.setZ( to.getZ() );
		return from;
	}
	//***********************************World Checking/Common Condition Checking for Listeners to also respect factions settings***********************//
	/**
	 * Checks if world is an ignored PvP world in faction config
	 * @param world
	 * @return boolean (true if it is an ignored world)
	 */
	/**
	 * Checks if powerloss in world is disabled in config
	 * @param world
	 * @return boolean (true if it is a powerloss disabled world)
	 */
	
	public static final void setPeaceful(Faction faction) {
		setPeaceful(faction, Boolean.TRUE);
	}

	public static final void setPeaceful(Faction faction, Boolean state) {
		assert Q.nn( faction );
		Bridge.factions.setFlag( faction, FactionsAny.FFlag.PEACEFUL,  state );
	}
	
	public static final boolean isPeaceful(Faction faction) {
		return Bridge.factions.getFlag( faction, FactionsAny.FFlag.PEACEFUL );
	}
	
	/**
	 * use this method instead of Conf.wildernessPowerLoss
	 * @return
	 */
	public static final boolean confIs_wildernessPowerLoss() {
		//technically, you don't need the faction for 1.6.x, but you do for 1.7.x version of Factions
		return Bridge.factions.getFlag(Factions.i.getNone(), FactionsAny.FFlag.POWERLOSS);
	}
	
	/**
	 * use this method instead of Conf.warZonePowerLoss
	 * @return
	 */
	public static final boolean confIs_warzonePowerLoss() {
		//technically, you don't need the faction for 1.6.x, but you do for 1.7.x version of Factions
		return Bridge.factions.getFlag(Factions.i.get(ID_WARZONE), FactionsAny.FFlag.POWERLOSS);
	}
	
}
