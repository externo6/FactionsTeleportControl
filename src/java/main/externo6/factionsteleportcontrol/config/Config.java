package externo6.factionsteleportcontrol.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Event;

import externo6.factionsteleportcontrol.FactionsTeleportControl;
import externo6.factionsteleportcontrol.FactionsTeleportControlPlugin;
import externo6.factionsteleportcontrol.config.sections.Section_Teleports;
import externo6.factionsteleportcontrol.config.sections.Section_Extras;
import externo6.factionsteleportcontrol.config.sections._boolean;
import externo6.factionsteleportcontrol.config.yaml.WYComment;
import externo6.factionsteleportcontrol.config.yaml.WYIdentifier;
import externo6.factionsteleportcontrol.config.yaml.WYItem;
import externo6.factionsteleportcontrol.config.yaml.WYRawButLeveledLine;
import externo6.factionsteleportcontrol.config.yaml.WYSection;
import externo6.factionsteleportcontrol.config.yaml.WY_IDBased;
import externo6.factionsteleportcontrol.config.yaml.WannabeYaml;
import externo6.factionsteleportcontrol.events.FPConfigLoadedEvent;
import externo6.factionsteleportcontrol.util.Q;
import externo6.factionsteleportcontrol.util.RethrownException;



public abstract class Config {
	public static final File				folderBase				= new File( "plugins" + File.separator + "FactionsTeleportControl" );
	public final static File						fileConfig				= new File( Config.folderBase, "config.yml" );
	public static final char				DOT						= '.';
	@Section(
			 realAlias_neverDotted = "Teleports" )
	public static final Section_Teleports	_teleports				= new Section_Teleports();
    @Section(
            realAlias_neverDotted = "extras" )
public final static Section_Extras                _extras                                        = new Section_Extras();
	
	@Option(
		autoComment={
			"if true it will remove all auto comments which explain what each option does in the config file."
			,"The config file header is not removed."
			,"This option is here only for those that want to increase readability in the config file."
		},
		realAlias_inNonDottedFormat = "disableAutoCommentsInConfig" )
	public static final _boolean disableAutoCommentsInConfig=new _boolean(false);
	private static final Class				configClass				= Config.class;
	
	
	
	private static File						currentFolder_OnPluginClassInit;
	private static File						currentFolder_OnEnable	= null;
	
	
	private static boolean					inited					= false;
	private static boolean					loaded= false;
	private static String[]	fpConfigHeaderArray;

	public final static void init() {
		assert !isLoaded();
		setInited( false );
		boolean failed = false;
		// try {
		if ( Q.isInconsistencyFileBug() ) {
			throw FactionsTeleportControlPlugin.bailOut( "Please do not have `user.dir` property set, it will mess up so many things"
				+ "(or did you use native functions to change current folder from the one that was on jvm startup?!)" );
		}
		
		if ( hasFileFieldsTrap() ) {
			throw FactionsTeleportControlPlugin.bailOut( "there is a coding trap which will likely cause unexpected behaviour "
				+ "in places that use files, tell plugin author to fix" );
		}

		Typeo.sanitize_AndUpdateClassMapping( configClass );
		fpConfigHeaderArray	= new String[]{
			FactionsTeleportControl.instance.getDescription().getFullName()+" configuration file"
		};
		
		for ( int i = 0; i < fpConfigHeaderArray.length; i++ ) {
			if (fpConfigHeaderArray[i].contains("\n") || fpConfigHeaderArray[i].contains( "\r" ) ) {
				throw FactionsTeleportControl.bailOut( "Do not use newlines inside the header, " +
						"but instead add a new element line in the array. Problematic line #"+i+"\n`"+fpConfigHeaderArray[i]+"`" );
			}
		}
		
		setInited( true);
	}

	private static boolean hasFileFieldsTrap() {
		Class classToCheckFor_FileFields = Config.class;
		Field[] allFields = classToCheckFor_FileFields.getFields();
		for ( Field field : allFields ) {
			if ( File.class.equals( field.getType() ) ) {
				// got one File field to check
				try {
					File instance = (File)field.get( classToCheckFor_FileFields );
					if ( instance.getPath().isEmpty() ) {
						// oops, found one, to avoid traps where you expect new File( instance, yourfile);
						// to have 'yourfile' in root folder of that drive ie. '\yourfile' instead of what you might
						// expect "yourfile" to be just in current folder just like a new File(yourfile) would do
						return true;
					}
				} catch ( IllegalArgumentException e ) {
					Q.rethrow( e );
				} catch ( IllegalAccessException e ) {
					Q.rethrow( e );
				}
			}
		}
		return false;
	}

	public synchronized final static void reload() {
		
		Config.setLoaded( false );// must be here to cause config to reload on every plugin(s) reload from console
		boolean failed = false;
		try {
			
			Config.ensureFoldersExist();
			
			reloadConfig();
						
			// last:
			Config.setLoaded( true );
			// Create the event here
			Event event = new FPConfigLoadedEvent();
			// Call the event
			Bukkit.getServer().getPluginManager().callEvent(event);
		} catch ( Throwable t ) {
			Q.rethrow( t );
		} finally {
			if ( failed ) {
				FactionsTeleportControl.instance.disableSelf();
			}
		}
	}
	
	

	
	protected static void ensureFoldersExist() {
		File dataF = FactionsTeleportControl.instance.getDataFolder();
		if ( !dataF.equals( folderBase ) ) {
			throw FactionsTeleportControlPlugin
				.bailOut( "Base folder and dataFolder differ, this may not be intended and it may just be a possible bug in the code;"
					+ "folderBase=" + folderBase + " dataFolder=" + dataF );
		}
		
		try {
			addDir( Config.folderBase );
			
		} catch ( Exception e ) {
			throw FactionsTeleportControlPlugin.bailOut(e, "something failed when ensuring the folders exist" );
		}
	}
	
	
	private static final void addDir( File dir ) {
		if ( !dir.exists() ) {
			if ( dir.getPath().isEmpty() ) {
				throw FactionsTeleportControlPlugin.bailOut( "bad coding, this should usually not trigger here, but earlier" );
			}
			FactionsTeleportControlPlugin.info( "Added directory: " + dir );
			dir.mkdirs();
		}
	}
	
	
	
	private final static String	bucketOfSpaces	= new String( new char[WannabeYaml.maxLevelSpaces] ).replace( '\0', ' ' );
	
	private final static void parseWrite( int level, Map<String, Object> start ) throws IOException {
		for ( Map.Entry<String, Object> entry : start.entrySet() ) {
			Object val = entry.getValue();
			String key = entry.getKey();
			if ( level > 0 ) {
				bw.write( bucketOfSpaces, 0, WannabeYaml.spacesPerLevel * level );
			}
			bw.write( key );
			bw.write( WannabeYaml.IDVALUE_SEPARATOR );
			if ( !( val instanceof MemorySection ) ) {
				bw.write( " " + val );
				bw.newLine();
			} else {
				bw.newLine();
				parseWrite( level + 1, ( (MemorySection)val ).getValues( false ) );
			}
		}
	}
	
	
	
	private final static void appendSection( int level, WYSection root ) throws IOException {
		assert Q.nn( root );
		WYItem currentItem = root.getFirst();
		
		while ( null != currentItem ) {
			
			Class<? extends WYItem> cls = currentItem.getClass();
			// System.out.println(currentItem+"!");
			
			if ( level > 0 ) {
				bw.write( bucketOfSpaces, 0, WannabeYaml.spacesPerLevel * level );
			}
			
			if ( currentItem instanceof WYRawButLeveledLine ) {
				bw.write( ( (WYRawButLeveledLine)currentItem ).getRawButLeveledLine() );
				bw.newLine();
			} else {
				
				if ( !( currentItem instanceof WY_IDBased ) ) {
					throw FactionsTeleportControl.bailOut( "impossible, coding bug detected" );
				}
				
				
				if ( WYIdentifier.class == cls ) {
					WYIdentifier wid = ( (WYIdentifier)currentItem );
					// System.out.println(wid.getInAbsoluteDottedForm(virtualRoot));
					bw.write( wid.getId() );
					bw.write( WannabeYaml.IDVALUE_SEPARATOR );
					bw.write( WannabeYaml.space + wid.getValue() );
					bw.newLine();
				} else {
					if ( WYSection.class == cls ) {
						WYSection cs = (WYSection)currentItem;
						bw.write( ( cs ).getId() + WannabeYaml.IDVALUE_SEPARATOR );
						bw.newLine();
						appendSection( level + 1, cs );// recurse
					} else {
						throw FactionsTeleportControl.bailOut( "impossible, coding bug detected" );
					}
				}
			}
			currentItem = currentItem.getNext();
		}
	}
	
	private final static void parseOneTime_and_CheckForValids( WYSection root, String dottedParentSection ) {
		assert Q.nn( root );
		WYItem<COMetadata> currentItem = root.getFirst();
		boolean isTopLevelSection = ( null == dottedParentSection ) || dottedParentSection.isEmpty();
		
		while ( null != currentItem ) {
			
			Class<? extends WYItem> cls = currentItem.getClass();
			
			
			if ( WYSection.class == cls ) {
				WYSection cs = (WYSection)currentItem;
				String dotted = ( isTopLevelSection ? cs.getId() : dottedParentSection + Config.DOT + cs.getId() );
				
				parseOneTime_and_CheckForValids( cs, dotted );
			} else {
				if ( WYIdentifier.class == cls ) {
					WYIdentifier<COMetadata> wid = ( (WYIdentifier)currentItem );
					String dotted = ( isTopLevelSection ? wid.getId() : dottedParentSection + Config.DOT + wid.getId() );
					Field foundAsField = Typeo.getField_correspondingTo_DottedFormat( dotted );
					if ( null == foundAsField ) {
						COMetadata oldmd = wid.setMetadata( new CO_Invalid( wid, dotted ) );
						assert null == oldmd : "should not already have metadata, else we failed somewhere else";
					} else {
						WYIdentifier<COMetadata> prevWID = mapFieldToID.shyAddWIDToSet( dotted, wid, foundAsField );
						if ( null != prevWID ) {

							int activeLine = prevWID.getLineNumber();
							
							COMetadata oldmd = wid.setMetadata( new CO_Duplicate( wid, dotted, prevWID ) );
							assert null == oldmd : "should not already have metadata, else we failed somewhere else";
						} else {
							wid.setMetadata( new CO_FieldPointer( foundAsField, wid, true ) );
						}
						
					}
					
				} else {
					assert ( currentItem instanceof WYRawButLeveledLine );

				}
			}
			
			currentItem = currentItem.getNext();
		}
	}
	
	private static BufferedWriter	bw;
	
	
	public final static void saveConfig() {
		try {

			
			FileOutputStream fos = null;
			OutputStreamWriter osw = null;
			bw = null;
			try {
				fos = new FileOutputStream( Config.fileConfig);
				osw = new OutputStreamWriter( fos, Q.UTF8 );
				bw = new BufferedWriter( osw );
				appendSection( 0, virtualRoot );
			} catch ( IOException e ) {
				Q.rethrow( e );
			} finally {
				if ( null != bw ) {
					try {
						bw.close();
					} catch ( IOException e ) {
						e.printStackTrace();
					}
				}
				if ( null != osw ) {
					try {
						osw.close();
					} catch ( IOException e ) {
						e.printStackTrace();
					}
				}
				if ( null != fos ) {
					try {
						fos.close();
					} catch ( IOException e ) {
						e.printStackTrace();
					}
				}
			}
		} catch ( RethrownException e ) {
			e.printStackTrace();
			throw FactionsTeleportControlPlugin.bailOut( "could not save config file: " + Config.fileConfig.getAbsolutePath() );
		}
	}
	
	protected static WYSection	virtualRoot		= null;
	
	private static final HM1	mapFieldToID	= new HM1();//store all except newly added fields
	private static final String	AUTOCOMMENTS_PREFIX	= "### ";
	private static final String	BEGINNING_OF_NEXT_CFG_OPTION	= "###########################################################";

	public synchronized final static boolean reloadConfig() {
		
		if ( Config.fileConfig.exists() ) {
			if ( !Config.fileConfig.isFile() ) {
				throw FactionsTeleportControlPlugin.bailOut( "While '" + Config.fileConfig.getAbsolutePath()
					+ "' exists, it is not a file!" );
			}
		}else {
			try {
				Config.fileConfig.createNewFile();
			} catch ( IOException e ) {
				FactionsTeleportControl.bailOut(e, "Cannot create config file "+Config.fileConfig.getAbsolutePath() );
			}
			
		}
		
		
		try {
			
			virtualRoot = WannabeYaml.read( fileConfig );
			cleanAutoComments( virtualRoot, null );
			synchronized ( mapFieldToID ) {
				synchronized ( Typeo.lock1 ) {
					mapFieldToID.clear();
					
					parseOneTime_and_CheckForValids( virtualRoot, null );
					parseSecondTime_and_sortOverrides( virtualRoot );
					mapFieldToID.clear();
					
				}
			}
			
		} catch ( IOException e ) {

			throw FactionsTeleportControlPlugin.bailOut( e, "failed to load existing config file '" + Config.fileConfig.getAbsolutePath()
				+ "'" );
		}
			
		prependHeader(virtualRoot);
		setFieldValuesToThoseFromConfig();
		
		if (!Config.disableAutoCommentsInConfig._){
			addAutoCommentsInConfig();
		}

		applyChangesInsideConfig();
		
		saveConfig();

		if (encountered>SKIP_AFTER_ENCOUNTERED) {
			FactionsTeleportControl.warn( "Skipped "+ChatColor.RED+(encountered-SKIP_AFTER_ENCOUNTERED)+ChatColor.RESET
				+" more messages due to being over the limit of "+SKIP_AFTER_ENCOUNTERED );
		}
		encountered=0;
		virtualRoot=null;
		return true;
	}
	
	
	private static void addAutoCommentsInConfig() {
		synchronized ( Typeo.lock1 ) {
			howManyWeSet=0;
			
			parseAndAddAutoComments(virtualRoot,null);
			
			assert Typeo.orderedListOfFields.size() == howManyWeSet:"not all/or more fields were set: "+
					howManyWeSet+" / "+Typeo.orderedListOfFields.size();
			
		}
	}

	private static void parseAndAddAutoComments( WYSection root, String dottedParentSection) {
		assert Q.nn( root );
		WYItem<COMetadata> currentItem = root.getFirst();
		boolean isTopLevelSection = ( null == dottedParentSection ) || dottedParentSection.isEmpty();
		
		while ( null != currentItem ) {
			WYItem nextItem = currentItem.getNext();//this is because currentItem will change and have it's next not point to old next:)
			
			Class<? extends WYItem> cls = currentItem.getClass();
			
			
			if ( WYSection.class == cls ) {
				WYSection cs = (WYSection)currentItem;
				String dotted = ( isTopLevelSection ? cs.getId() : dottedParentSection + Config.DOT + cs.getId() );
				
				parseAndAddAutoComments( cs , dotted );// recurse
			} else {
				if ( WYIdentifier.class == cls ) {
					WYIdentifier<COMetadata> wid = ( (WYIdentifier)currentItem );
					COMetadata meta = wid.getMetadata();
					if ( ( null != meta ) && ( meta.getClass().equals(CO_FieldPointer.class) ) ) {
						howManyWeSet++;
						String dotted = ( isTopLevelSection ? wid.getId() : dottedParentSection + Config.DOT + wid.getId() );
						Field field = Typeo.getField_correspondingTo_DottedFormat( dotted );
						assert null != field:"something went wrong somewhere else field not found for: "+dotted;
						String[] comments = Typeo.getComments( field );
						prependComments( root, wid, dotted, comments, field );
					}
				} else {
					assert ( currentItem instanceof WYRawButLeveledLine );
				}
			}
			
			currentItem = nextItem;
		}
	}


	private static int howManyWeSet=0;
	private static void setFieldValuesToThoseFromConfig() {
		synchronized ( Typeo.lock1 ) {
			howManyWeSet=0;
		
			parseAndSetFields(virtualRoot);
		
			assert Typeo.orderedListOfFields.size() == howManyWeSet:"not all/or more fields were set: "+
					howManyWeSet+" / "+Typeo.orderedListOfFields.size();
		}
	}
	
	private static void parseAndSetFields( WYSection root ) {
		assert Q.nn( root );
		WYItem<COMetadata> currentItem = root.getFirst();

		
		while ( null != currentItem ) {
			WYItem nextItem = currentItem.getNext();
			
			Class<? extends WYItem> cls = currentItem.getClass();
			
			
			if ( WYSection.class == cls ) {
				WYSection cs = (WYSection)currentItem;
				assert null == cs.getMetadata() : "this should not have metadata, unless we missed something";
				parseAndSetFields( cs );
			} else {
				if ( WYIdentifier.class == cls ) {
					WYIdentifier<COMetadata> wid = ( (WYIdentifier)currentItem );
					COMetadata meta = wid.getMetadata();
					if ( null != meta ) {
						if ( meta.getClass().equals( CO_FieldPointer.class)  ) {
							CO_FieldPointer fpmeta = (CO_FieldPointer)meta;
							try {
								howManyWeSet++;
								assert fpmeta.wid.getValue().equals(wid.getValue());
								Typeo.setFieldValue( fpmeta.field, wid.getValue() );
							} catch ( Throwable t ) {
								if ( t.getClass().equals( NumberFormatException.class )
									|| t.getClass().equals( BooleanFormatException.class ) )
								{
									Q.rethrow( new InvalidConfigValueTypeException( wid, fpmeta.field, t ) );
								} else {
									Q.rethrow( new FailedToSetConfigValueException( wid, fpmeta.field, t ) );
								}
							}
						}
					}
				} else {
					assert ( currentItem instanceof WYRawButLeveledLine );
				}
			}
			
			currentItem = nextItem;
		}
	}
	

	private static void prependHeader( WYSection root ) {
		for ( int j = fpConfigHeaderArray.length-1; j >= 0; j-- ) {
			String line = fpConfigHeaderArray[j];
			root.prepend( getAsAutoComment(line) );
		}
	}
	
	private static final WYComment<COMetadata> getAsAutoComment(String line) {
		return new WYComment<COMetadata>( 0, AUTOCOMMENTS_PREFIX+line );
	}
	
	private static final boolean isAutoComment(String line) {
		if ( (null == line) || (line.isEmpty()) ) {
			return false;
		}
		return line.startsWith( AUTOCOMMENTS_PREFIX );
	}

	private final static void cleanAutoComments( WYSection root, String dottedParentSection ) {
		assert Q.nn( root );
		WYItem<COMetadata> currentItem = root.getFirst();
		boolean isTopLevelSection = ( null == dottedParentSection ) || dottedParentSection.isEmpty();
		
		WYItem nextItem;
		while ( null != currentItem ) {
			nextItem=currentItem.getNext();
			Class<? extends WYItem> cls = currentItem.getClass();
			
			
			if ( WYSection.class == cls ) {
				WYSection cs = (WYSection)currentItem;
				String dotted = ( isTopLevelSection ? cs.getId() : dottedParentSection + Config.DOT + cs.getId() );
				
				cleanAutoComments( cs, dotted );// recurse
			} else {
				if (currentItem instanceof WYRawButLeveledLine) {
					if (cls == WYComment.class) {
						WYComment comment=(WYComment)currentItem;
						if (isAutoComment( comment.getRawButLeveledLine() ) ){
//							System.out.println(comment.getRawButLeveledLine());
							root.remove( comment );
						}
					}
				}
			}
			
			currentItem =nextItem;
		}
	}

	private static WYSection createWYRootFromFields() {
		Q.ni();
		return virtualRoot;
		
	}


	private static void applyChangesInsideConfig() {
		virtualRoot.recalculateLineNumbers();
		parseAndApplyChanges( virtualRoot );
	}
	
	
	@SuppressWarnings( "boxing" )
	private static void parseAndApplyChanges( WYSection root ) {
		assert Q.nn( root );
		WYItem<COMetadata> currentItem = root.getFirst();
		
		while ( null != currentItem ) {
			WYItem nextItem = currentItem.getNext();
			
			Class<? extends WYItem> cls = currentItem.getClass();
			
			
			if ( WYSection.class == cls ) {
				WYSection cs = (WYSection)currentItem;
				assert null == cs.getMetadata() : "this should not have metadata, unless we missed something";
				parseAndApplyChanges( cs );
			} else {
				if ( WYIdentifier.class == cls ) {
					WYIdentifier<COMetadata> wid = ( (WYIdentifier)currentItem );
					COMetadata meta = wid.getMetadata();
					if ( null != meta ) {
						while (true) {
							Class<? extends COMetadata> metaClass = meta.getClass();
							if ( metaClass.equals( CO_Duplicate.class ) ) {
								CO_Duplicate metaDup = (CO_Duplicate)meta;
								metaDup.appliesToWID.getParent().replaceAndTransformInto_WYComment( metaDup.appliesToWID,
									metaDup.commentPrefixForDUPs );
								
								Config
									.warn( "Duplicate config option encountered at line "
										+ metaDup.colorLineNumOnDuplicate
										+ metaDup.appliesToWID.getLineNumber()
										+ ChatColor.RESET
										+ " and this was transformed into comment so that you can review it & know that it was ignored.\n"
										+ metaDup.colorOnDuplicate + metaDup.appliesToWID.toString() + "\n" + ChatColor.RESET
										+ "the option at line " + ChatColor.AQUA + metaDup.theActiveFirstWID.getLineNumber()
										+ ChatColor.RESET + " overriddes this duplicate with value " + ChatColor.AQUA
										+ metaDup.theActiveFirstWID.getValue() );
								
								break;
							}
							
							if ( metaClass.equals( CO_Invalid.class ) ) {
								CO_Invalid metaInvalid = (CO_Invalid)meta;
								metaInvalid.appliesToWID.getParent().replaceAndTransformInto_WYComment(
									metaInvalid.appliesToWID, metaInvalid.commentPrefixForINVALIDs );
								Config.warn( "Invalid config option\n" + metaInvalid.colorOnINVALID
									+ metaInvalid.thePassedDottedFormatForThisWID + ChatColor.RESET
									+ " was auto commented at line "
									+ metaInvalid.colorOnINVALID + metaInvalid.appliesToWID.getLineNumber() + '\n'// +ChatColor.RESET
									+ metaInvalid.colorOnINVALID + metaInvalid.appliesToWID.toString() );
								
								break;
							}
							
							if ( metaClass.equals( CO_Overridden.class ) ) {
								CO_Overridden metaOverridden = (CO_Overridden)meta;
								metaOverridden.lostOne.getParent()
									.replaceAndTransformInto_WYComment(
										metaOverridden.lostOne,
										String.format( metaOverridden.commentPrefixForOVERRIDDENones,
											metaOverridden.dottedOverriddenByThis,
											metaOverridden.overriddenByThis.getLineNumber() ) );
								
								Config.warn( "Config option " + ChatColor.AQUA + metaOverridden.dottedOverriddenByThis
									+ ChatColor.RESET + " at line " + ChatColor.AQUA
									+ metaOverridden.overriddenByThis.getLineNumber() + ChatColor.RESET
									+ " overrides the old alias for it `" + ChatColor.DARK_AQUA + metaOverridden.dottedLostOne
									+ ChatColor.RESET + "` which is at line " + ChatColor.DARK_AQUA
									+ metaOverridden.lostOne.getLineNumber() + ChatColor.RESET
									+ " which was also transformed into comment to show it's ignored." );
								
								break;
							}
							
							if ( metaClass.equals( CO_Upgraded.class ) ) {
								CO_Upgraded metaUpgraded = (CO_Upgraded)meta;
								metaUpgraded.upgradedWID.getParent().replaceAndTransformInto_WYComment(
									metaUpgraded.upgradedWID,
									String.format( metaUpgraded.commentPrefixForUPGRADEDones, metaUpgraded.theNewUpgradeDotted,
										metaUpgraded.wid.getLineNumber() ) );
								
								Config.info( "Upgraded `" + ChatColor.DARK_AQUA + metaUpgraded.upgradedDotted + ChatColor.RESET
									+ "` of line `" + ChatColor.DARK_AQUA + metaUpgraded.upgradedWID.getLineNumber()
									+ ChatColor.RESET + "` to the new config name of `"
									+ metaUpgraded.COLOR_FOR_NEW_OPTIONS_ADDED + metaUpgraded.theNewUpgradeDotted
									+ ChatColor.RESET + "` of line `" + metaUpgraded.COLOR_FOR_NEW_OPTIONS_ADDED
									+ metaUpgraded.wid.getLineNumber() + "`" );
								
								break;
							}
							

							break;
						}

					}
				} else {
					assert ( currentItem instanceof WYRawButLeveledLine );
					
				}
			}
			
			currentItem = nextItem;
		}
	}
	
	
	@SuppressWarnings("null")
	private static void parseSecondTime_and_sortOverrides( WYSection vroot ) {
		synchronized ( mapFieldToID ) {
			synchronized ( Typeo.lock1 ) {
				Field field = null;
				for ( Iterator iterator = Typeo.orderedListOfFields.iterator(); iterator.hasNext(); ) {
					field = (Field)iterator.next();
					String[] orderOfAliases = Typeo.getListOfOldAliases( field );
					String dottedRealAlias = Typeo.getDottedRealAliasOfField( field );
					assert null != orderOfAliases;

					SetOfIDs aSet = mapFieldToID.get( field );
					if ( null == aSet ) {
						WYIdentifier<COMetadata> x = putFieldValueInTheRightWYPlace( vroot, Typeo.getFieldDefaultValue( field ), dottedRealAlias);
						assert null != x;
						COMetadata previousMD = x.setMetadata( new CO_FieldPointer( field, x, false) );
						assert null == previousMD:previousMD;
						FactionsTeleportControl.info( "Adding new config option\n`" +COMetadata.COLOR_FOR_NEW_OPTIONS_ADDED+ dottedRealAlias + ChatColor.RESET+"`" );
						continue;
					}

					String dottedOverrider = null;
					
					WYIdentifier<COMetadata> overriderWID = aSet.get( dottedRealAlias );
					if ( null == overriderWID ) {
						for ( int i = 0; i < orderOfAliases.length; i++ ) {
							overriderWID = aSet.get( orderOfAliases[i] );
							if ( null != overriderWID ) {
								dottedOverrider = orderOfAliases[i];
								break;
							}
						}
						assert ( null != overriderWID ) : "if the real alias wasn't in list, then at least "
							+ "one oldalias that is not .equals to realAlias would've been found and set";
					} else {
						dottedOverrider = dottedRealAlias;
					}					
					assert ( null != overriderWID );
					assert ( null != dottedOverrider );
					
					Set<Entry<String, WYIdentifier<COMetadata>>> iter = aSet.entrySet();
					for ( Entry<String, WYIdentifier<COMetadata>> entry : iter ) {
						WYIdentifier<COMetadata> wid = entry.getValue();
						if ( overriderWID != wid ) {
							
							String widDotted = entry.getKey();
							COMetadata previousMD =
								wid.setMetadata( new CO_Overridden( wid, widDotted, overriderWID, dottedOverrider ) );
							assert ( CO_FieldPointer.class.isAssignableFrom( previousMD.getClass() ) ) : "this should be the only way"
								+ "that the wid we got here had a previously associated metadata with it, aka it has to have the"
								+ "pointer to the Field";
							CO_FieldPointer fp = (CO_FieldPointer)previousMD;
							Field pfield = fp.field;
							assert null != pfield;
							assert pfield.equals( field ) : "should've been the same field, else code logic failed";
							
						}
					}
					assert !aSet.isEmpty();
					aSet.clear();
					
					assert null != overriderWID;
					
					if (!dottedOverrider.equals(dottedRealAlias)) {
						
						String valueToCarry = overriderWID.getValue();
						
						WYIdentifier<COMetadata> old = overriderWID;
						
						overriderWID=putFieldValueInTheRightWYPlace( vroot, valueToCarry, dottedRealAlias );
						assert null != overriderWID;
						COMetadata pre = overriderWID.setMetadata( new CO_FieldPointer( field, overriderWID, false) );//we still have to set the field to its default value
						assert null == pre:pre;
						
						COMetadata previousMD = old.setMetadata( new CO_Upgraded( old, dottedOverrider, field, overriderWID, dottedRealAlias ) );
						dottedOverrider=dottedRealAlias;
						assert ( CO_FieldPointer.class.isAssignableFrom( previousMD.getClass() ) ) : "this should be the only way"
							+ "that the wid we got here had a previously associated metadata with it, aka it has to have the"
							+ "pointer to the Field";
						CO_FieldPointer fp = (CO_FieldPointer)previousMD;
						Field pfield = fp.field;
						assert null != pfield;
						assert pfield.equals( field ) : "should've been the same field, else code logic failed";
						
					}
					assert null != overriderWID;
					assert Typeo.isValidAliasFormat( dottedOverrider );
					aSet.put( dottedOverrider, overriderWID );
					assert aSet.size() == 1;
				}
			}
		}
	}
	
	private static WYIdentifier putFieldValueInTheRightWYPlace( WYSection vroot, String value, String dottedRealAlias) {
		assert Q.nn( vroot );
		assert Q.nn( value );
		assert Typeo.isValidAliasFormat( dottedRealAlias );
		WYSection foundParentSection = parseCreateAndReturnParentSectionFor( vroot,  dottedRealAlias );
		assert null != foundParentSection : "impossible, it should've created and returned a parent even if it didn't exist";
		int index = dottedRealAlias.lastIndexOf( Config.DOT );
		String lastPartOfRealAlias;
		if ( index >= 0 ) {// well not really 0
			lastPartOfRealAlias = dottedRealAlias.substring( 1 + index );
		}else{
			lastPartOfRealAlias=dottedRealAlias;
		}
		assert Typeo.isValidAliasFormat( lastPartOfRealAlias ) : lastPartOfRealAlias;
		WYIdentifier leaf = new WYIdentifier<COMetadata>( 0, lastPartOfRealAlias, value );
		foundParentSection.append( leaf );

		return leaf;
	}
	
	
	private static final void prependComments(WYSection parentOfChild, WYIdentifier<COMetadata> beforeThisChild,
		String dottedAlias, String[] comments, Field field) {
		assert null != parentOfChild;
		assert null != beforeThisChild;
		assert beforeThisChild.getParent() == parentOfChild;
		assert null != comments;
		parentOfChild.insertBefore( getAsAutoComment( "" ), beforeThisChild );
		parentOfChild.insertBefore( getAsAutoComment( "# "+dottedAlias ), beforeThisChild );
		parentOfChild.insertBefore( getAsAutoComment( "default value: "+Typeo.getFieldDefaultValue( field )), beforeThisChild );
//		}
		
		for ( int i = 0; i < comments.length; i++ ) {
			parentOfChild.insertBefore( getAsAutoComment( comments[i] ), beforeThisChild );
		}
	}
	
	private static WYSection parseCreateAndReturnParentSectionFor( WYSection root, String dottedID ) {
		assert Q.nn( root );
		
		
		WYItem<COMetadata> currentItem = root.getFirst();
		int index = dottedID.indexOf( Config.DOT );
		if ( index < 0 ) {
			return root;
		}
		String findCurrent = dottedID.substring( 0, index );
		
		while ( null != currentItem ) {
			
			Class<? extends WYItem> cls = currentItem.getClass();
			
			
			if ( WYSection.class == cls ) {
				WYSection cs = (WYSection)currentItem;
				if ( findCurrent.equals( cs.getId() ) ) {
					return parseCreateAndReturnParentSectionFor( cs, dottedID.substring( 1 + index ) );// recurse
				}
			} else {
				if ( WYIdentifier.class == cls ) {
					WYIdentifier<COMetadata> wid = ( (WYIdentifier)currentItem );
					if ( findCurrent.equals( wid.getId() ) ) {
						throw new RuntimeException(
							"bad parameters for this method, because you searched for parent aka section, and we found it as an id" );
					}
				} else {
					assert ( currentItem instanceof WYRawButLeveledLine );
				}
			}
			
			currentItem = currentItem.getNext();
		}
		WYSection<COMetadata> parent = new WYSection<COMetadata>( 0, findCurrent );
		root.append( parent );
		return parseCreateAndReturnParentSectionFor( parent,  dottedID.substring( 1 + index ) );
	}
	
	
	public static boolean isInited() {
		return inited;
	}
	
	private static void setInited( boolean nowState ) {
		inited = nowState;
	}
	
	private static void setLoaded( boolean nowState ) {
		loaded = nowState;
	}
	
	public static boolean isLoaded() {
		return loaded;
	}
	

	public static void deInit() {
		if (isInited()) {
			if (isLoaded()) {
				setLoaded( false );
			}
			setInited( false );
		}
	}
	
	private static final int SKIP_AFTER_ENCOUNTERED=300;
	private static int encountered=0;
	protected static void info(String msg) {
		encountered++;
		if ( encountered <= SKIP_AFTER_ENCOUNTERED ) {
			FactionsTeleportControl.info( msg );
		}
	}
	
	
	protected static void warn(String msg) {
		encountered++;
		if ( encountered <= SKIP_AFTER_ENCOUNTERED ) {
			FactionsTeleportControl.warn(msg);
		}
	}
}
