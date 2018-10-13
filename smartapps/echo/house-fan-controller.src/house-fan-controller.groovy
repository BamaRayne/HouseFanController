/**
 *  House Fan Controller
 *
 *  Copyright 2018 bamarayne
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *
 *	10/13/2018		Version:1.0 R.0.0.5		Bug fixes - messages played with every window open/close
 *	10/12/2018		Version:1.0 R.0.0.4		Icons updates and code cleanup
 *	10/12/2018		Version:1.0 R.0.0.3		Added "Auto On Mode". Fan turns on and actions are run when xx number of windows are opened.
 *	10/12/2018		Version:1.0 R.0.0.2		Added additional safety measures. Minimum windows open required and fan auto shut off
 *	09/26/2018		Version:1.0 R.0.0.1		Alpha
 */
definition(
    name: "House Fan Controller",
    namespace: "Echo",
    author: "bamarayne",
    description: "Quality Control of your homes ventilation system",
    category: "Convenience",
	iconUrl			: "https://raw.githubusercontent.com/BamaRayne/HouseFanController/icons/Main.png",
	iconX2Url		: "https://raw.githubusercontent.com/BamaRayne/HouseFanController/icons/Main2x.png",
	iconX3Url		: "https://raw.githubusercontent.com/BamaRayne/HouseFanController/icons/Main2x.png")


preferences {
	page name: "mainPage"
    page name: "condPage"
    page name: "actionsPage"
    page name: "settingsPage"
    page name: "onPage"
    page name: "offPage"
    page name: "condFailPage"
    page name: "certainTime"
    }

/******************************************************************************
	MAIN PAGE
******************************************************************************/
def mainPage() {	
    dynamicPage(name: "mainPage", title:"", install: true, uninstall: false) {
        section ("Primary Fan") {
            input "priFan", "capability.switch", title: "Select your Whole House Fan", multiple: false, required: false, submitOnChange: true,
            image: "https://raw.githubusercontent.com/BamaRayne/HouseFanController/icons/Fan.jpg"
        }
        if (priFan) {
            section ("Conditions") {
                href "condPage", title: "Verify these Conditions have been met (only when fan is turned on)", description: condPageComplete(), state: condPageSettings(),
                image: "https://raw.githubusercontent.com/BamaRayne/HouseFanController/icons/Yellow%20Light.png"
            }
        }
        if (priFan) {
            section ("Conditions Met Actions") {
                href "actionsPage", title: "Perform these actions when all conditions are met", description: actionsPageComplete(), state: actionsPageSettings(),
                image: "https://raw.githubusercontent.com/BamaRayne/HouseFanController/icons/Green%20Light.png"
            }
        }
        section ("Conditions Failure Actions") {
            href "condFailPage", title: "Perform these actions when conditions have failed", description: condFailPageComplete(), state: condFailPageSettings(),
            image: "https://raw.githubusercontent.com/BamaRayne/HouseFanController/icons/Red%20Lights.png"
        }        
        section ("Settings") {
            href "settingsPage", title: "App and Safety Settings", description: settingsPageComplete(), state: settingsPageSettings(),
            image: "https://raw.githubusercontent.com/BamaRayne/HouseFanController/icons/Safety.jpg"
        }
    }    
}

/******************************************************************************
	ACTIONS PAGE
******************************************************************************/
def actionsPage() {
    dynamicPage(name: "actionsPage", title: "Configure Actions",install: false, uninstall: false) {
        section ("Actions when Fan turns On") {
            href "onPage", title: "Perform these actions when fan is turned on", description: actionsOnPageComplete(), state: actionsOnPageSettings(),
            image: "https://raw.githubusercontent.com/BamaRayne/HouseFanController/icons/Start.png"
        }
        section ("Actions when Fan turns Off") {
            href "offPage", title: "Perform these actions when fan is turned off", description: actionsOffPageComplete(), state: actionsOffPageSettings(),
            image: "https://raw.githubusercontent.com/BamaRayne/HouseFanController/icons/Stop.png"
        }
    }
}    
		
/******************************************************************************
	SETTINGS PAGE
******************************************************************************/
def settingsPage() {
    dynamicPage(name: "settingsPage", title: "Configure App Settings",install: false, uninstall: true) {
        section ("App Settings") {
            input "logs", "bool", title: "Show logs in the IDE Live Logging", defaultValue: false, submitOnChange: true,
            image: "https://raw.githubusercontent.com/BamaRayne/HouseFanController/icons/Log.png"
        }
        if (safety) {
        section ("Auto On Mode") {
        	input "auto", "bool", title: "Automatically turn on fan when $cContactWindowMin window(s) have been opened",defaultValue: false, submitOnChange: true,
            image: "https://raw.githubusercontent.com/BamaRayne/HouseFanController/icons/Auto.png"
            }
        }    
        section ("Safety") {
            input "safety", "bool", title: "Do you have a gas furnace, water heater, or other pilot flame device?", defaultValue: false, submitOnChange: true,
            image: "https://raw.githubusercontent.com/BamaRayne/HouseFanController/icons/Warn.png"
        }
        if (safety == true) {
            section ("Disclaimer") {
                    paragraph "**** IT IS THE OWNERS RESPONSIBILITY TO SAFELY OPERATE APPLIANCES WITHIN YOUR HOME. THE AUTHOR " +
                    "OF THIS PROGRAM CAN NOT BE HELD RESPONSIBLE FOR YOUR ACTIONS. YOUR USE OF THIS APPLICATION IS YOUR " +
                    "ACKNOWLEDGEMENT THAT YOU WILL NOT HOLD THE PROGRAM AUTHOR ACCOUNTABLE AND THAT YOU HAVE READ THESE WARNINGS.*** " +
                    " \n" +
                    " \n" +
                 	"You have indicated that you have gas appliances in your home. \n" +
                    "Most home gas appliances have a pilot light and exhuast through a plume " +
                    "to the outside. If there is not adequate ventilation for your whole house fan, it can " +
                    "potentially cause an exhaust backflow situation in which CO2 is pulled into your home. " +
                    "Because of this, additional safety measures have been added ---> 1) If there is not adequate " +
                    "ventilation the fan will be turned off. 2) You must select windows and a minimum amount of open " +
                    "windows required. 3) When the minimum number of open windows is no longer met due to windows being " +
                    "closed, the fan will be turned off. 4) House Fan Controller defaults to a minimum of One Open Window " 
                    
            }
        }    
    }   
}  

/******************************************************************************
	CONDITIONS CONFIGURATION PAGE
******************************************************************************/
def condPage() {
    dynamicPage(name: "condPage", title: "Perform these actions when all conditions have been satisfied",install: false, uninstall: false) {
        section ("Location Settings Conditions") {
            input "cMode", "mode", title: "Location Mode is...", multiple: true, required: false, submitOnChange: true
            input "cDays", title: "Days of the week", multiple: true, required: false, submitOnChange: true,
                "enum", options: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]
            href "certainTime", title: "Time Schedule", description: pTimeComplete(), state: pTimeSettings()
        }         
        section ("Switches") {
            input "cSwitch", "capability.switch", title: "Switches", multiple: true, submitOnChange: true, required:false
            if (cSwitch) {
                input "cSwitchCmd", "enum", title: "are...", options:["on":"On","off":"Off"], multiple: false, required: true, submitOnChange: true
                if (cSwitch?.size() > 1) {
                    input "cSwitchAll", "bool", title: "Activate this toggle if you want ALL of the switches to be $tSwitchCmd as a condition.", required: false, defaultValue: false, submitOnChange: true
                }
            }
        }
        section ("Presence Conditions") {
            input "cPresence", "capability.presenceSensor", title: "Presence Sensors", multiple: true, required: false, submitOnChange: true
            if (cPresence) {
                input "cPresenceCmd", "enum", title: "are...", options: ["present":"Present","not present":"Not Present"], multiple: false, required: true, submitOnChange: true
                if (cPresence?.size() > 1) {
                    input "cPresenceAll", "bool", title: "Activate this toggle if you want ALL of the Presence Sensors to be $cPresenceCmd as a condition.", required: false, defaultValue: false, submitOnChange: true
                }
            }
        }
        section ("Doors") {
            input "cContactDoor", "capability.contactSensor", title: "Contact Sensors only on Doors", multiple: true, required: false, submitOnChange: true
            if (cContactDoor) {
                input "cContactDoorCmd", "enum", title: "that are...", options: ["open":"open", "closed":"closed"], multiple: false, required: true, submitOnChange: true
                if (cContactDoor?.size() > 1) {
                    input "cContactDoorAll", "bool", title: "Activate this toggle if you want ALL of the Doors to be $cContactDoorCmd as a condition.", required: false, defaultValue: false, submitOnChange: true
                }
            }
        }
        section ("Windows") {
            input "cContactWindow", "capability.contactSensor", title: "Contact Sensors only on Windows", multiple: true, required: false, submitOnChange: true
            if (cContactWindow) {
                input "cContactWindowCmd", "enum", title: "that are...", options: ["open":"open", "closed":"closed"], multiple: false, required: true, submitOnChange: true
                if (cContactWindow?.size() > 1) {
                    input "cContactWindowAll", "bool", title: "Activate this toggle if you want ALL of the Windows to be $cContactWindowCmd as a condition.", required: false, defaultValue: false, submitOnChange: true
                    input "cContactWindowMin", "number", title: "Minimum number of windows that must be open?", required: false, defaultValue: 1, submitOnChange: true
                }
            }
        }
        section ("Environmental Conditions") {
            input "cHumidity", "capability.relativeHumidityMeasurement", title: "Relative Humidity", required: false, submitOnChange: true
            if (cHumidity) input "cHumidityLevel", "enum", title: "Only when the Humidity is...", options: ["above", "below"], required: false, submitOnChange: true            
            if (cHumidityLevel) input "cHumidityPercent", "number", title: "this level...", required: true, description: "percent", submitOnChange: true            
            if (cHumidityPercent) input "cHumidityStop", "number", title: "...but not ${cHumidityLevel} this percentage", required: false, description: "humidity"
            input "cTemperature", "capability.temperatureMeasurement", title: "Temperature", required: false, multiple: true, submitOnChange: true
            if (cTemperature) input "cTemperatureLevel", "enum", title: "When the temperature is...", options: ["above", "below"], required: false, submitOnChange: true
            if (cTemperatureLevel) input "cTemperatureDegrees", "number", title: "Temperature...", required: true, description: "degrees", submitOnChange: true
            if (cTemperatureDegrees) input "cTemperatureStop", "number", title: "...but not ${cTemperatureLevel} this temperature", required: false, description: "degrees"
        }
    }
} 


/***********************************************************************************************************
   CONDITIONS FAIL PAGE
************************************************************************************************************/
def condFailPage() {
    dynamicPage(name: "condFailPage", title: "Perform these actions when conditions have not been met.", install: false, uninstall: false) {
        section ("Conditions Fail Alert Message") {
            input "failMsg", "text", title: "Send this message when the conditions have not been met", required: false, submitOnChange: true
        }
        section ("Send Conditions Failed Message to") {
            input "synthDevice", "capability.speechSynthesis", title: "Speech Synthesis Devices", multiple: true, required: false
            input "echoDevice", "capability.notification", title: "Amazon Alexa Devices", multiple: true, required: false
            input "sonosDevice", "capability.musicPlayer", title: "Music Player Devices", required: false, multiple: true, submitOnChange: true    
            if (sonosDevice) {
                input "volume", "number", title: "Temporarily change volume", description: "0-100% (default value = 30%)", required: false
            }
            input "sendText", "bool", title: "Enable Text Notifications", required: false, submitOnChange: true     
            if (sendText){      
                paragraph "You may enter multiple phone numbers separated by comma to deliver the Alexa message. E.g. +18045551122,+18046663344"
                input name: "sms", title: "Send text notification to (optional):", type: "phone", required: false
            }
            input "push", "bool", title: "Send Push Notification (optional)", required: false, defaultValue: false            
        }
    }
}    

/***********************************************************************************************************
   ACTIONS ON PAGE
************************************************************************************************************/
def onPage() {
    dynamicPage(name: "onPage", title: "Perform these actions when the whole house fan is turned on.", install: false, uninstall: false) {
        section ("Fans (non-adjustable)") {
                input "aFans", "capability.switch", title: "These Fans...", multiple: true, required: false, submitOnChange: true
                if (aFans) {
                    input "aFansCmd", "enum", title: "...will...", options:["on":"turn on","off":"turn off"], multiple: false, required: false, submitOnChange:true
                    if (aFansCmd=="on") {
                        input "aFansDelayOn", "number", title: "Delay turning on by this many seconds", defaultValue: 0, submitOnChange:true
                        if (aFansDelayOn) input "aFansPendOn", "bool", title: "Activate for pending state change cancellation", required: false, default: false
                    }
                    if (aFansCmd=="off") {
                        input "aFansDelayOff", "number", title: "Delay turning off by this many seconds", defaultValue: 0, submitOnChange:true
                        if (aFansDelayOff) input "aFansPendOff", "bool", title: "Activate for pending state change cancellation", required: false, default: false
                    	}
                    }
                }
            section ("Ceiling Fans (adjustable)") {
                input "aCeilingFans", "capability.switchLevel", title: "These ceiling fans...", multiple: true, required: false, submitOnChange: true
                if (aCeilingFans) {
                    input "aCeilingFansCmd", "enum", title: "...will...", options:["on":"turn on","off":"turn off","low":"set to low","med":"set to med","high":"set to high","incr":"speed up","decr":"slow down"], multiple: false, required: false, submitOnChange:true
                    if (aCeilingFansCmd == "incr") {
                        input "aCeilingFansIncr", "number", title: "...by this percentage", required: true, submitOnChange: true
                    }
                    if (aCeilingFansCmd == "decr") {
                        input "aCeilingFansDecr", "number", title: "...by this percentage", required: true, submitOnChange: true
                    }
                }
            }
        	section ("Thermostats") {
                input "cTstat", "capability.thermostat", title: "...and these thermostats will...", multiple: true, required: false, submitOnChange:true
                if (cTstat) {
                    input "cTstatFan", "enum", title: "...set the fan mode to...", options:["auto":"auto","on":"on","off":"off","circ":"circulate"], multiple: false, required: false, submitOnChange:true
                    input "cTstatMode", "enum", title: "...set the operating mode to...", options:["cool":"cooling","heat":"heating","auto":"auto","on":"on","off":"off","incr":"increase","decr":"decrease"], multiple: false, required: false, submitOnChange:true
                    if (cTstatMode in ["cool","auto"]) { input "coolLvl", "number", title: "Cool Setpoint", required: true, submitOnChange: true}
                    if (cTstatMode in ["heat","auto"]) { input "heatLvl", "number", title: "Heat Setpoint", required: true, submitOnChange: true}
                    if (cTstatMode in ["incr","decr"]) {
                        if (cTstatMode == "decr") {paragraph "NOTE: This will decrease the temp from the current room temp minus what you choose."}
                        if (cTstatMode == "incr") {paragraph "NOTE: This will increase the temp from the current room temp plus what you choose."}
                        input "tempChange", "number", title: "By this amount...", required: true, submitOnChange: true }
                }}
            section("More Thermostats") {
                    input "cTstat1", "capability.thermostat", title: "More Thermostat(s)...", multiple: true, required: false, submitOnChange:true
                    if (cTstat1) {
                        input "cTstat1Fan", "enum", title: "Fan Mode", options:["auto":"Auto","on":"On","off":"Off","circ":"Circulate"],multiple: false, required: false, submitOnChange:true
                        input "cTstat1Mode", "enum", title: "Operating Mode", options:["cool":"Cool","heat":"Heat","auto":"Auto","on":"On","off":"Off","incr":"Increase","decr":"Decrease"],multiple: false, required: false, submitOnChange:true
                        if (cTstat1Mode in ["cool","auto"]) { input "coolLvl1", "number", title: "Cool Setpoint", required: true, submitOnChange: true }
                        if (cTstat1Mode in ["heat","auto"]) { input "heatLvl1", "number", title: "Heat Setpoint", required: true, submitOnChange: true }
                        if (cTstat1Mode in ["incr","decr"]) {
                            if (cTstat1Mode == "decr") {paragraph "NOTE: This will decrease the temp from the current room temp minus what you choose."}
                            if (cTstat1Mode == "incr") {paragraph "NOTE: This will increase the temp from the current room temp plus what you choose."}
                            input "tempChange1", "number", title: "By this amount...", required: true, submitOnChange: true }
                    }
                }
            section ("Vents") {
                input "aVents", "capability.switchLevel", title: "These vents...", multiple: true, required: false, submitOnChange: true
                if (aVents) {
                    input "aVentsCmd", "enum", title: "...will...",
                        options:["on":"open","off":"close","25":"change to 25% open","50":"change to 50% open","75":"change to 75% open"], multiple: false, required: false, submitOnChange:true
                }
            }
            section ("Shades"){
                input "aShades", "capability.windowShade", title: "These window coverings...", multiple: true, required: false, submitOnChange: true
                if (aShades) {
                    input "aShadesCmd", "enum", title: "...will...", options:["on":"open","off":"close","25":"change to 25% oetn","50":"change to 50% open","75":"change to 75% open"], multiple: false, required: false, submitOnChange:true
                }
            }
        }
    }
    
/***********************************************************************************************************
   ACTIONS OFF PAGE
************************************************************************************************************/
def offPage() {
    dynamicPage(name: "offPage", title: "Perform these actions when the house fan is turned off.", install: false, uninstall: false) {
        section("Simple On/Off/Toggle Switches") {    
            input "aOtherSwitchesOff", "capability.switch", title: "On/Off/Toggle Lights & Switches", multiple: true, required: false, submitOnChange: true
            if (aOtherSwitchesOff) {
                input "aOtherSwitchesCmdOff", "enum", title: "...will turn...", options: ["on":"on","off":"off","toggle":"toggle"], multiple: false, required: false, submitOnChange: true
            }
        }    
        section("More Simple On/Off/Toggle Switches") {    
            input "aOtherSwitches2Off", "capability.switch", title: "On/Off/Toggle Lights & Switches", multiple: true, required: false, submitOnChange: true
            if (aOtherSwitches2Off) {
                input "aOtherSwitchesCmd2Off", "enum", title: "...will turn...", options: ["on":"on","off":"off","toggle":"toggle"], multiple: false, required: false, submitOnChange: true
            }
        }
    	section ("Dimmers - Selection") {    
        	input "aDimOff", "capability.switchLevel", title: "Dimmable Lights and Switches", multiple: true, required: false , submitOnChange:true
        	if (aDimOff) {
            	input "aDimCmdOff", "enum", title: "...will...", options:["on":"turn on","off":"turn off","set":"set the level","decrease":"decrease","increase":"increase"], multiple: false, required: false, submitOnChange: true
            if (aDimCmdOff=="decrease") {
                input "aDimDecreaseOff", "number", title: "the lights by this %", required: false, submitOnChange: true
            }
            if (aDimCmdOff == "increase") {
                input "aDimIncreaseOff", "number", title: "the lights by this %", required: false, submitOnChange: true
            }
            if (aDimCmdOff == "set") {
                input "aDimLVLOff", "number", title: "...of the lights to...", description: "this percentage", range: "0..100", required: false, submitOnChange: true
            }
            input "aDimDelayOff", "number", title: "Delay this action by this many seconds.", required: false, defaultValue: 0, submitOnChange: true
        }
    }    
    section("More Dimmers - Selection") {
        input "aOtherDimOff", "capability.switchLevel", title: "More Dimmers", multiple: true, required: false , submitOnChange:true
        if (aOtherDimOff) {
            input "aOtherDimCmdOff", "enum", title: "...will...", options:["on":"turn on","off":"turn off","set":"set the level","decrease":"decrease","increase":"brighten"], multiple: false, required: false, submitOnChange:true
            if (aOtherDimCmdOff=="decrease") {
                input "aOtherDimDecreaseOff", "number", title: "the lights by this %", required: false, submitOnChange: true
            }
            if (aOtherDimCmdOff == "increase") {
                input "aOtherDimIncreaseOff", "number", title: "the lights by this %", required: false, submitOnChange: true
            }
            if (aOtherDimCmdOff == "set") {
                input "aOtherDimLVLOff", "number", title: "...of the lights to...", description: "this percentage", range: "0..100", required: false, submitOnChange: true
            }
            input "otherDimDelayOff", "number", title: "Delay this action by this many seconds.", required: false, defaultValue: 0, submitOnChange: true
        }
    }
    section ("Fans connected to switches") {
        input "aFansOff", "capability.switch", title: "These Fans...", multiple: true, required: false, submitOnChange: true
        if (aFansOff) {
            input "aFansCmdOff", "enum", title: "...will...", options:["on":"turn on","off":"turn off"], multiple: false, required: false, submitOnChange:true
            if (aFansCmdOff=="on") {
                input "aFansDelayOnOff", "number", title: "Delay turning on by this many seconds", defaultValue: 0, submitOnChange:true
                //           if (aFansDelayOnOff) input "aFansPendOnOff", "bool", title: "Activate for pending state change cancellation", required: false, default: false
            }
            if (aFansCmdOff=="off") {
                input "aFansDelayOffOff", "number", title: "Delay turning off by this many seconds", defaultValue: 0, submitOnChange:true
                //           if (aFansDelayOffOff) input "aFansPendOffOff", "bool", title: "Activate for pending state change cancellation", required: false, default: false
            }
        }
    }
    section ("Fans and Ceiling Fan Settings (adjustable)") {
        input "aCeilingFansOff", "capability.switchLevel", title: "These ceiling fans...", multiple: true, required: false, submitOnChange: true
        if (aCeilingFansOff) {
            input "aCeilingFansCmdOff", "enum", title: "...will...", options:["on":"turn on","off":"turn off","low":"set to low","med":"set to med","high":"set to high","incr":"speed up","decr":"slow down"], multiple: false, required: false, submitOnChange:true
            if (aCeilingFansCmdOff == "incr") {
                input "aCeilingFansIncrOff", "number", title: "...by this percentage", required: true, submitOnChange: true
            }
            if (aCeilingFansCmdOff == "decr") {
                input "aCeilingFansDecrOff", "number", title: "...by this percentage", required: true, submitOnChange: true
            }
        }
    }
    section ("Thermostat") {
        input "cTstatOff", "capability.thermostat", title: "...and these thermostats will...", multiple: true, required: false, submitOnChange:true
        if (cTstatOff) {
            input "cTstatFanOff", "enum", title: "...set the fan mode to...", options:["auto":"auto","on":"on","off":"off","circ":"circulate"], multiple: false, required: false, submitOnChange:true
            input "cTstatModeOff", "enum", title: "...set the operating mode to...", options:["cool":"cooling","heat":"heating","auto":"auto","on":"on","off":"off","incr":"increase","decr":"decrease"], multiple: false, required: false, submitOnChange:true
            if (cTstatModeOff in ["cool","auto"]) { input "coolLvl", "number", title: "Cool Setpoint", required: true, submitOnChange: true}
            if (cTstatModeOff in ["heat","auto"]) { input "heatLvl", "number", title: "Heat Setpoint", required: true, submitOnChange: true}
            if (cTstatModeOff in ["incr","decr"]) {
                if (cTstatModeOff == "decr") {paragraph "NOTE: This will decrease the temp from the current room temp minus what you choose."}
                if (cTstatModeOff == "incr") {paragraph "NOTE: This will increase the temp from the current room temp plus what you choose."}
                input "tempChangeOff", "number", title: "By this amount...", required: true, submitOnChange: true }
        }
    }
    if(cTstatOff) {
        section("ThermostatsOff") {
            input "cTstat1Off", "capability.thermostat", title: "More Thermostat(s)...", multiple: true, required: false, submitOnChange:true
            if (cTstat1Off) {
                input "cTstat1FanOff", "enum", title: "Fan Mode", options:["auto":"Auto","on":"On","off":"Off","circ":"Circulate"],multiple: false, required: false, submitOnChange:true
                input "cTstat1ModeOff", "enum", title: "Operating Mode", options:["cool":"Cool","heat":"Heat","auto":"Auto","on":"On","off":"Off","incr":"Increase","decr":"Decrease"],multiple: false, required: false, submitOnChange:true
                if (cTstat1ModeOff in ["cool","auto"]) { input "coolLvl1", "number", title: "Cool Setpoint", required: true, submitOnChange: true }
                if (cTstat1ModeOff in ["heat","auto"]) { input "heatLvl1", "number", title: "Heat Setpoint", required: true, submitOnChange: true }
                if (cTstat1ModeOff in ["incr","decr"]) {
                    if (cTstat1ModeOff == "decr") {paragraph "NOTE: This will decrease the temp from the current room temp minus what you choose."}
                    if (cTstat1ModeOff == "incr") {paragraph "NOTE: This will increase the temp from the current room temp plus what you choose."}
                    input "tempChange1Off", "number", title: "By this amount...", required: true, submitOnChange: true }
            }
        }
    }
    section ("Vents") {
        input "aVentsOff", "capability.switchLevel", title: "These vents...", multiple: true, required: false, submitOnChange: true
        if (aVentsOff) {
            input "aVentsCmdOff", "enum", title: "...will...",
                options:["on":"open","off":"close","25":"change to 25% open","50":"change to 50% open","75":"change to 75% open"], multiple: false, required: false, submitOnChange:true
        }
    }
    section ("Shades"){
        input "aShadesOff", "capability.windowShade", title: "These window coverings...", multiple: true, required: false, submitOnChange: true
        if (aShadesOff) {
            input "aShadesCmdOff", "enum", title: "...will...", options:["on":"open","off":"close","25":"change to 25% oetn","50":"change to 50% open","75":"change to 75% open"], multiple: false, required: false, submitOnChange:true
        }
    }
}
}



/************************************************************************************************************
	Base Process
************************************************************************************************************/
def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
	if(priFan && safety){ subscribe(priFan, "switch.on", safetyCheck) }
    	else { subscribe(priFan, "switch.on", conditionHandler) } 
    
    subscribe(priFan, "switch.off", processOffActions) 
	if(cContactWindow && safety){ subscribe(cContactWindow, "contact.closed", safetyHandler) }
	if(auto) {subscribe(cContactWindow, "contact.open", autoMode) }
}

/***********************************************************************************************************
   SAFETY CHECK 
************************************************************************************************************/
def safetyCheck(evt) {
    def devList = []
    def safetyCheck = true
    def safetyTime = 2
    def msg = "Hey, The $priFan is being turned off due to there not being adequate ventilation available. Please open some windows and select those " +
        "windows in the House Fan Controller app."
    log.warn "Performing Safety Check by Verifying proper ventilation due to gas appliances present"
    if (cContactWindow == null) { 
        sendPush(msg)
        runIn(safetyTime, safetyMethod)
        ttsActions(msg)
        return
    }
    def cContactWindowSize = cContactWindow?.size()
    cContactWindow.each { deviceName ->
        def status = deviceName.currentValue("contact")
        if (status == "open"){ 
            String device  = (String) deviceName
            devList += device
        }
    }
    def devListSize = devList?.size()
    if (devListSize == 0) { 
        safetyCheck = false
    }
    if (safetyCheck == true) {
        conditionHandler(evt)
    }
    if (safetyCheck == false) {
        runIn(safetyTime, safetyMethod)
        if (failMsg == null) {
            ttsActions(msg)
            sendPush(msg)
        }
        else {
            ttsActions(failMsg)
        }
    }
    log.info "Safety check is $safetyCheck"
}

/***********************************************************************************************************
   SAFETY HANDLER
************************************************************************************************************/
def safetyHandler(evt) {
log.info "safetyHandler called due to windows being closed."
	def msg = "The $priFan is being turned off due to the number of open windows falling below the number of windows required to be open. " +
    "The minimum number of required windows is, $cContactWindowMin and there are currently $devList?size()"
    def devList = []
    def fanStatus = priFan.currentValue("switch")
    log.info "fanStatus = $fanStatus"
    if (cContactWindow) {
    	if (fanStatus == "on") {
        def cContactWindowSize = cContactWindow?.size()
        cContactWindow.each { deviceName ->
            def status = deviceName.currentValue("contact")
            if (status == "open"){  
                String device  = (String) deviceName
                devList += device
                log.info "devList = $devList"
            }
        }
        def devListSize = devList?.size()
        if(!cContactWindowAll) {
            if (devListSize < cContactWindowMin) { 
                log.info "Windows devListSize of $cContactWindowMin NOT met, turning off Fans"
                safetyMethod()
                ttsActions(msg)
            }
        }        
        if(cContactWindowAll) {
            if (devListSize < cContactWindowSize) { 
                safetyMethod() 
            	ttsActions(msg)
            	}
            }
        }
    }
}

/***********************************************************************************************************
   CONDITIONS HANDLER
************************************************************************************************************/
def autoMode(evt) {
	log.info "autoMode method called"
    def devList = []
    if (cContactWindow) {
        def cContactWindowSize = cContactWindow?.size()
        cContactWindow.each { deviceName ->
            def status = deviceName.currentValue("contact")
            if (status == "open"){  
                String device  = (String) deviceName
                devList += device
                log.info "devList = $devList"
            }
        }
       	def devListSize = devList.size()
        	if (devListSize == cContactWindowMin) {
            priFan.on()
            def msg = "The whole house fan has been automatically turned on due to $cContactWindowMin windows being opened"
            ttsActions(msg)
            }
        }    
	}
/***********************************************************************************************************
   CONDITIONS HANDLER
************************************************************************************************************/
def conditionHandler(evt) {
    def result
    def cSwitchOk = false
    def cHumOk = false
    def cTempOk = false
    def cModeOk = false
    def cPresenceOk = false
    def cDoorOk = false
    def cWindowOk = false
    def cDaysOk = false
    def cPendAll = false
    def timeOk = false
    def cGarageOk = false
    def devList = []
    def safetyTime = 5
//    def cContactWindowMin
    def msg = "The $priFan is being turned off due to your preset conditions having not been met. Please see the House Fan Controller app for more information."

    log.info "Verifying Conditions:"

    // SWITCHES
    if (cSwitch == null) { cSwitchOk = true }
    if (cSwitch) {
        log.trace "Conditions: Switches events method activated"
        def cSwitchSize = cSwitch?.size()
        cSwitch.each { deviceName ->
            def status = deviceName.currentValue("switch")
            if (status == "${cSwitchCmd}"){ 
                String device  = (String) deviceName
                devList += device
            }
        }
        def devListSize = devList?.size()
        if(!cSwitchAll) {
            if (devList?.size() > 0) { 
                cSwitchOk = true  
            }
        }        
        if(cSwitchAll) {
            if (devListSize == cSwitchSize) { 
                cSwitchOk = true 
            }
        }
        if (cSwitchOk == false) log.warn "Switches Conditions Handler failed"
    }

    // HUMIDITY
    if (cHumidity == null) {cHumOk = true }
    if (cHumidity) {
        log.trace "Conditions: Humidity events method activated"
        int cHumidityStopVal = cHumidityStop == null ? 0 : cHumidityStop as int
            cHumidity.each { deviceName ->
                def status = deviceName.currentValue("humidity")
                if (cHumidityLevel == "above") {
                    cHumidityStopVal = cHumidityStopVal == 0 ? 999 :  cHumidityStopVal as int
                        if (status >= cHumidityPercent && status <= cHumidityStopVal) {
                            cHumOk = true
                        }
                }
                if (cHumidityLevel == "below") {
                    if (status <= cHumidityPercent && status >= cHumidityStopVal) {
                        cHumOk = true
                    }
                }    
            }
        if (cHumOk == false) log.warn "Humidity Conditions Handler failed"
    }

    // TEMPERATURE
    if (cTemperature == null) {cTempOk = true }
    if (cTemperature) {
        log.trace "Conditions: Temperature events method activated"
        int cTemperatureStopVal = cTemperatureStop == null ? 0 : cTemperatureStop as int
            cTemperature.each { deviceName ->
                def status = deviceName.currentValue("temperature")
                if (cTemperatureLevel == "above") {
                    cTemperatureStopVal = cTemperatureStopVal == 0 ? 999 :  cTemperatureStopVal as int
                        if (status >= cTemperatureDegrees && status <= cTemperatureStopVal) {
                            cTempOk = true
                        }
                }
                if (cTemperatureLevel == "below") {
                    if (status <= cTemperatureDegrees && status >= cTemperatureStopVal) {
                        cTempOk = true
                    }
                }    
            }
        if (cTempOk == false) log.warn "Temperature Conditions Handler failed"
    }	

    // DAYS OF THE WEEK
    if (cDays == null) { cDaysOk = true }
    if (cDays) {
        log.trace "Conditions: Days of the Week events method activated"
        def df = new java.text.SimpleDateFormat("EEEE")
        if (location.timeZone) {
            df.setTimeZone(location.timeZone)
        }
        else {
            df.setTimeZone(TimeZone.getTimeZone("America/New_York"))
        }
        def day = df.format(new Date())
        if (cDaysOk == false) log.warn "Days Conditions Handler failed"
        result = cDays.contains(day)
    }

    // LOCATION MODE
    if (cMode == null) { cModeOk = true }
    if (cMode) {
        log.trace "Conditions: Mode events method activated"
        cModeOk = !cMode || cMode?.contains(location.mode)
        if (cModeOk == false) log.warn "Mode Conditions Handler failed"
    }

    // PRESENCE
    if (cPresence == null) { cPresenceOk = true }
    if (cPresence) {
        log.trace "Conditions: Presence events method activated"
        def cPresenceSize = cPresence.size()
        cPresence.each { deviceName ->
            def status = deviceName.currentValue("presence")
            if (status == cPresenceCmd){ 
                String device  = (String) deviceName
                devList += device
            }
        }
        def devListSize = devList?.size()
        if(!cPresenceAll) {
            if (devList?.size() > 0) { 
                cPresenceOk = true  
            }
        }        
        if(cPresenceAll) {
            if (devListSize == cPresenceSize) { 
                cPresenceOk = true 
            }
        }
        if (cPresenceOk == false) log.warn "Presence Conditions Handler failed"
    }

    // DOOR CONTACT SENSORS
    if (cContactDoor == null) { cDoorOk = true }
    if (cContactDoor) {
        log.trace "Conditions: Door Contacts events method activated"
        def cContactDoorSize = cContactDoor?.size()
        cContactDoor.each { deviceName ->
            def status = deviceName.currentValue("contact")
            if (status == "${cContactDoorCmd}"){ 
                String device  = (String) deviceName
                devList += device
            }
        }
        def devListSize = devList?.size()
        if(!cContactDoorAll) {
            if (devList?.size() > 0) { 
                cDoorOk = true  
            }
        }        
        if(cContactDoorAll) {
            if (devListSize == cContactDoorSize) { 
                cDoorOk = true 
            }
        }
        if (cDoorOk == false) log.warn "Door Contacts Conditions Handler failed"
    }

    // WINDOW CONTACT SENSORS
    if (cContactWindow == null) { cWindowOk = true }
    if (cContactWindow) {
        log.trace "Conditions: Window Contacts events method activated"
        def cContactWindowSize = cContactWindow?.size()
        cContactWindow.each { deviceName ->
            def status = deviceName.currentValue("contact")
            if (status == "open"){  
                String device  = (String) deviceName
                devList += device
            }
        }
        def devListSize = devList?.size()
        if(!cContactWindowAll) {
            if (devListSize >= cContactWindowMin) { 
                log.info "Windows devListSize = $devListSize and the minimum is = $cContactWindowMin"
                cWindowOk = true  
            }
            else {
                cWindowOk = false
                log.warn "Minimum of $cContactWindowMin windows are required to be open, there are $devList.size windows open"
            }
        }        
        if(cContactWindowAll) {
            if (devListSize == cContactWindowSize) { 
                cWindowOk = true 
            }
        }
        if (cWindowOk == false) log.warn "Window Contacts Conditions Handler failed"
    }

    // GARAGE DOORS
    if (cGarage == null) { cGarageOk = true }
    if (cGarage) {
        log.trace "Conditions: Garage Doors events method activated"
        cGarage.each { deviceName ->
            def status = deviceName.currentValue("door")
            if (status == "${cGarageCmd}"){
                cGarageOk = true
            }
            if (cGarageOk == false) log.warn "Garage Conditions Handler failed"
        }
    }    


    if (cGarageOk==true && cTempOk==true && cHumOk==true && cSwitchOk==true && cModeOk==true && 
        cPresenceOk==true && cDoorOk==true && cWindowOk==true && cDaysOk==true && getTimeOk(evt)==true) { 
        result = true
    }
    if (result == true) {
        log.warn "Conditions Verified ==> All Conditions have been met"
        processOnActions()
    } else {
        log.warn "Conditions Verified ==> All Conditions have NOT been met, $priFan will turn off in 10 seconds." 
        if (failMsg == null) {
            runIn(10, safetyMethod)
            ttsActions(msg)
        }
        else {
            ttsActions(failMsg)
            runIn(10, safetyMethod)
        }
    }
    return result
}
    
/***********************************************************************************************************************
	SAFETY METHOD THAT TURNS OFF FAN AUTOMATICALLY
***********************************************************************************************************************/
def safetyMethod() {
    log.info "Turning off the $priFan"
    priFan?.off()
    return
}

/***********************************************************************************************************************
	PROCESS ACTIONS HANDLER WHEN FAN TURNS ON
***********************************************************************************************************************/
def processOnActions(evt){
    log.info "Process On Actions Method activated."
//    if (conditionHandler()==true && getTimeOk()==true) {
        def result 
        def devList = []
        def aSwitchSize = aSwitch?.size()

        // OTHER SWITCHES
        if (aOtherSwitches) {
            if (aOtherSwitchesCmd == "on") {aOtherSwitches?.on()}
            if (aOtherSwitchesCmd == "off") {aOtherSwitches?.off()}
            if (aOtherSwitchesCmd == "toggle") {toggle2()}
        }
        if (aOtherSwitches2) {
            if (aOtherSwitchesCmd2 == "on") {aOtherSwitches2?.on()}
            if (aOtherSwitchesCmd2 == "off") {aOtherSwitches2?.off()}
            if (aOtherSwitchesCmd2 == "toggle") {toggle3()}
        }

        // DIMMERS
        if (aDim) {
            runIn(aDimDelay, dimmersHandler)
        }
        if (aOtherDim) { 
            runIn(otherDimDelay, otherDimmersHandler)
        }

        // CEILING FANS
        if (aCeilingFans) {
            if (aCeilingFansCmd == "on") {aCeilingFans.on()}
            else if (aCeilingFansCmd == "off") {aCeilingFans.off()}
            else if (aCeilingFansCmd == "low") {aCeilingFans.setLevel(33)}
            else if (aCeilingFansCmd == "med") {aCeilingFans.setLevel(66)}
            else if (aCeilingFansCmd == "high") {aCeilingFans.setLevel(99)}
            if (aCeilingFansCmd == "incr" && aCeilingFans) {
                def newLevel
                aCeilingFans?.each {deviceD ->
                    def currLevel = deviceD.latestValue("level")
                    newLevel = aCeilingFansIncr
                    newLevel = newLevel + currLevel
                    newLevel = newLevel < 0 ? 0 : newLevel > 99 ? 99 : newLevel
                    deviceD.setLevel(newLevel)
                }
            }
            if (aCeilingFansCmd == "decr" && aCeilingFans) {
                def newLevel
                aCeilingFans?.each {deviceD ->
                    def currLevel = deviceD.latestValue("level")
                    newLevel = aCeilingFansDecr
                    newLevel = currLevel - newLevel
                    newLevel = newLevel < 0 ? 0 : newLevel > 99 ? 99 : newLevel
                    deviceD.setLevel(newLevel)
                }
            }
        }
        // FANS
        if (aFansCmd == "on") { 
            runIn(aFansDelayOn, aFansOn) }
        if (aFansCmd == "off") {
            runIn(aFansDelayOff, aFansOff) }

        // VENTS
        if (aVents) {
            if (sVentsCmd == "on") {aVents.setLevel(100)}
            else if (aVentsCmd == "off") {aVents.off()}
            else if (aVentsCmd == "25") {aVents.setLevel(25)}
            else if (aVentsCmd == "50") {aVents.setLevel(50)}
            else if (aVentsCmd == "75") {aVents.setLevel(75)}
        }

        // WINDOW COVERINGS
        if (aShades) {
            if (aShadesCmd == "open") {aShades.setLevel(100)}
            else if (aShadesCmd == "close") {aShades.setLevel(0)}
            else if (aShadesCmd == "25") {aShades.setLevel(25)}
            else if (aShadesCmd == "50") {aShades.setLevel(50)}
            else if (aShadesCmd == "75") {aShades.setLevel(75)}
        }

        // THERMOSTATS
        if (cTstat) { thermostats() }
        if (cTstat1) { thermostats1() }
//    }    
}

/***********************************************************************************************************************
    DIMMERS HANDLER - FOR ACTIONS ON PROCESS
***********************************************************************************************************************/
def dimmersHandler() {
	if (logs) log.info "Dimmers Handler activated"
		if (aDim) {
            if (aDimCmd == "on") {aDim.on()}
            else if (aDimCmd == "off") {aDim.off()}
            if (aDimCmd == "set" && aDim) {
                def level = aDimLVL < 0 || !aDimLVL ?  0 : aDimLVL >100 ? 100 : aDimLVL as int
                    aDim.setLevel(level)
            }
            if (aDimCmd == "increase" && aDim) {
                def newLevel
                aDim?.each {deviceD ->
                    def currLevel = deviceD.latestValue("level")
                    newLevel = aDimIncrease
                    newLevel = newLevel + currLevel
                    newLevel = newLevel < 0 ? 0 : newLevel >100 ? 100 : newLevel
                    deviceD.setLevel(newLevel)
                }
            }
            if (aDimCmd == "decrease" && aDim) {
                def newLevel
                aDim?.each {deviceD ->
                    def currLevel = deviceD.latestValue("level")
                    newLevel = aDimDecrease
                    newLevel = currLevel - newLevel
                    newLevel = newLevel < 0 ? 0 : newLevel >100 ? 100 : newLevel
                    deviceD.setLevel(newLevel)
                }
            }
        }
    }

/***********************************************************************************************************************
    OTHER DIMMERS HANDLER - FOR ACTIONS ON PROCESS
***********************************************************************************************************************/
def otherDimmersHandler() {
	if (logs) log.info "Other Dimmers Handler activated"
        if (aOtherDim) {
            if (aOtherDimCmd == "on") {aOtherDim.on()}
            else if (aOtherDimCmd == "off") {aOtherDim.off()}
            if (aOtherDimCmd == "set" && aOtherDim) {
                def otherLevel = aOtherDimLVL < 0 || !aOtherDimLVL ?  0 : aOtherDimLVL >100 ? 100 : aOtherDimLVL as int
                    aOtherDim?.setLevel(otherLevel)
            }
            if (aOtherDimCmd == "increase" && aOtherDim) {
                def newLevel
                aOtherDim.each { deviceD ->
                    def currLevel = deviceD.latestValue("level")
                    newLevel = aOtherDimIncrease
                    newLevel = newLevel + currLevel
                    newLevel = newLevel < 0 ? 0 : newLevel >100 ? 100 : newLevel
                    deviceD.setLevel(newLevel)
                }
            }
            if (aOtherDimCmd == "decrease" && aOtherDim) {
                def newLevel
                aOtherDimCmd?.each { deviceD ->
                    def currLevel = deviceD.latestValue("level")
                    newLevel = aOtherDimDecrease
                    newLevel = currLevel - newLevel
                    newLevel = newLevel < 0 ? 0 : newLevel >100 ? 100 : newLevel
                    deviceD.setLevel(newLevel)
                }
            }
        }
	}

/************************************************************************************************************
FANS Handler  - FOR ACTIONS ON PROCESS
************************************************************************************************************/
def aFansOn(evt) {
    aFans?.on()
}
def aFansOff(evt) {
    aFans?.off()
}  

/************************************************************************************************************
THERMOSTATS HANDLERS - FOR ACTIONS ON PROCESS
************************************************************************************************************/
private thermostats(evt) {
    if (logs) log.info "thermostats handler method activated"
    cTstat.each {deviceD ->
        def currentMode = deviceD.currentValue("thermostatMode")
        def currentTMP = deviceD.currentValue("temperature")
        if (cTstatMode == "off") { cTstat.off()
                                 }
        if (cTstatMode == "auto" || cTstatMode == "on") {
            cTstat.auto()
            cTstat.setCoolingSetpoint(coolLvl)
            cTstat.setHeatingSetpoint(heatLvl)
        }
        if (cTstatMode == "cool") {
            cTstat.cool()
            cTstat.setCoolingSetpoint(coolLvl)
        }
        if (cTstatMode == "heat") {
            cTstat.heat()
            cTstat.setHeatingSetpoint(heatLvl)
        }
        if (cTstatMode == "incr") {
            def cNewSetpoint = tempChange
            cNewSetpoint = tempChange + currentTMP
            cNewSetpoint = cNewSetpoint < 60 ? 60 : cNewSetpoint > 85 ? 85 : cNewSetpoint
            def hNewSetpoint = tempChange
            hNewSetpoint = tempChange + currentTMP
            hNewSetpoint = hNewSetpoint < 60 ? 60 : hNewSetpoint > 85 ? 85 : hNewSetpoint
            if (currentMode == "auto" || currentMode == "on") {
                deviceD.setCoolingSetpoint(cNewSetpoint)
                deviceD.setHeatingSetpoint(hNewSetPoint)
            }
            if (currentMode == "cool") {
                deviceD.setCoolingSetpoint(cNewSetpoint)
            }
            if (currentMode == "heat") {
                deviceD.setHeatingSetpoint(hNewSetPoint)
            }
        }
        if (cTstatMode == "decr") {
            def cNewSetpoint = tempChange
            cNewSetpoint = currentTMP - tempChange
            cNewSetpoint = cNewSetpoint < 60 ? 60 : cNewSetpoint > 85 ? 85 : cNewSetpoint
            def hNewSetpoint = tempChange
            hNewSetpoint = currentTMP - tempChange
            hNewSetpoint = hNewSetpoint < 60 ? 60 : hNewSetpoint > 85 ? 85 : hNewSetpoint
            if (currentMode == "auto" || currentMode == "on") {
                deviceD.setCoolingSetpoint(cNewSetpoint)
                deviceD.setHeatingSetpoint(hNewSetPoint)
            }
            if (currentMode == "cool") {
                deviceD.setCoolingSetpoint(cNewSetpoint)
            }
            if (currentMode == "heat") {
                deviceD.setHeatingSetpoint(hNewSetPoint)
            }
        }
        if (cTstatFan == "auto" || cTstatFan == "off") { cTstat.fanAuto() }
        if (cTstatFan == "on") { cTstat.fanOn() }
        if (cTstatFan == "circ") { cTstat.fanCirculate() }
    }
}
private thermostats1(evt) {
    cTstat1.each {deviceD ->
        def currentMode = deviceD.currentValue("thermostatMode")
        def currentTMP = deviceD.currentValue("temperature")
        if (cTstat1Mode == "off") { cTstat1.off()
                                  }
        if (cTstat1Mode == "auto" || cTstat1Mode == "on") {
            cTstat1.auto()
            cTstat1.setCoolingSetpoint(coolLvl1)
            cTstat1.setHeatingSetpoint(heatLvl1)
        }
        if (cTstat1Mode == "auto" || cTstat1Mode == "on") {
            cTstat1.auto()
            cTstat1.setCoolingSetpoint(coolLvl1)
            cTstat1.setHeatingSetpoint(heatLvl1)
        }
        if (cTstat1Mode == "incr") {
            def cNewSetpoint = tempChange1
            cNewSetpoint = tempChange1 + currentTMP
            cNewSetpoint = cNewSetpoint < 60 ? 60 : cNewSetpoint > 85 ? 85 : cNewSetpoint
            def hNewSetpoint = tempChange1
            hNewSetpoint = tempChange1 + currentTMP
            hNewSetpoint = hNewSetpoint < 60 ? 60 : hNewSetpoint > 85 ? 85 : hNewSetpoint
            if (currentMode == "auto" || currentMode == "on") {
                deviceD.setCoolingSetpoint(cNewSetpoint)
                deviceD.setHeatingSetpoint(hNewSetPoint)
            }
            if (currentMode == "cool") {
                deviceD.setCoolingSetpoint(cNewSetpoint)
            }
            if (currentMode == "heat") {
                deviceD.setHeatingSetpoint(hNewSetPoint)
            }
        }
        if (cTsta1tMode == "decr") {
            def cNewSetpoint = tempChange1
            cNewSetpoint = currentTMP - tempChange1
            cNewSetpoint = cNewSetpoint < 60 ? 60 : cNewSetpoint > 85 ? 85 : cNewSetpoint
            def hNewSetpoint = tempChange1
            hNewSetpoint = currentTMP - tempChange1
            hNewSetpoint = hNewSetpoint < 60 ? 60 : hNewSetpoint > 85 ? 85 : hNewSetpoint
            if (currentMode == "auto" || currentMode == "on") {
                deviceD.setCoolingSetpoint(cNewSetpoint)
                deviceD.setHeatingSetpoint(hNewSetPoint)
            }
            if (currentMode == "cool") {
                deviceD.setCoolingSetpoint(cNewSetpoint)
            }
            if (currentMode == "heat") {
                deviceD.setHeatingSetpoint(hNewSetPoint)
            }
        }
        if (cTstat1Fan == "auto" || cTstat1Fan == "off") { cTstat1.fanAuto() }
        if (cTstat1Fan == "on") { cTstat1.fanOn() }
        if (cTstat1Fan == "circ") { cTstat1.fanCirculate() }
    }
}

/***********************************************************************************************************************
	PROCESS ACTIONS HANDLER WHEN FAN TURNS OFF
***********************************************************************************************************************/
def processOffActions(evt){
    log.info "Process Off Actions Method activated."
    def result 
    def devList = []
    def aSwitchSize = aSwitch?.size()

    // OTHER SWITCHES
    if (aOtherSwitchesOff) {
        if (aOtherSwitchesCmdOff == "on") {aOtherSwitchesOff?.on()}
        if (aOtherSwitchesCmdOff == "off") {aOtherSwitchesOff?.off()}
        if (aOtherSwitchesCmdOff == "toggle") {toggle2Off()}
    }
    if (aOtherSwitches2Off) {
        if (aOtherSwitchesCmd2Off == "on") {aOtherSwitches2Off?.on()}
        if (aOtherSwitchesCmd2Off == "off") {aOtherSwitches2Off?.off()}
        if (aOtherSwitchesCmd2Off == "toggle") {toggle3Off()}
    }

    // DIMMERS
    if (aDimOff) {
        runIn(aDimDelayOff, dimmersHandlerOff)
    }
    if (aOtherDimOff) { 
        runIn(otherDimDelayOff, otherDimmersHandlerOff)
    }

    // CEILING FANS
    if (aCeilingFansOff) {
        if (aCeilingFansCmdOff == "on") {aCeilingFansOff.on()}
        else if (aCeilingFansCmdOff == "off") {aCeilingFansOff.off()}
        else if (aCeilingFansCmdOff == "low") {aCeilingFansOff.setLevel(33)}
        else if (aCeilingFansCmdOff == "med") {aCeilingFansOff.setLevel(66)}
        else if (aCeilingFansCmdOff == "high") {aCeilingFansOff.setLevel(99)}
        if (aCeilingFansCmdOff == "incr" && aCeilingFans) {
            def newLevel
            aCeilingFansOff?.each {deviceD ->
                def currLevel = deviceD.latestValue("level")
                newLevel = aCeilingFansIncrOff
                newLevel = newLevel + currLevel
                newLevel = newLevel < 0 ? 0 : newLevel > 99 ? 99 : newLevel
                deviceD.setLevel(newLevel)
            }
        }
        if (aCeilingFansCmdOff == "decr" && aCeilingFansOff) {
            def newLevel
            aCeilingFansOff?.each {deviceD ->
                def currLevel = deviceD.latestValue("level")
                newLevel = aCeilingFansDecrOff
                newLevel = currLevel - newLevel
                newLevel = newLevel < 0 ? 0 : newLevel > 99 ? 99 : newLevel
                deviceD.setLevel(newLevel)
            }
        }
    }
    // FANS
    if (aFansCmdOff == "on") { 
        runIn(aFansDelayOnOff, aFansOnOff) }
    if (aFansCmdOff == "off") {
        runIn(aFansDelayOffOff, aFansOffOff) }

    // VENTS
    if (aVentsOff) {
        if (sVentsCmdOff == "on") {aVentsOff.setLevel(100)}
        else if (aVentsCmdOff == "off") {aVentsOff.off()}
        else if (aVentsCmdOff == "25") {aVentsOff.setLevel(25)}
        else if (aVentsCmdOff == "50") {aVentsOff.setLevel(50)}
        else if (aVentsCmdOff == "75") {aVentsOff.setLevel(75)}
    }

    // WINDOW COVERINGS
    if (aShadesOff) {
        if (aShadesCmdOff == "open") {aShadesOff.setLevel(100)}
        else if (aShadesCmdOff == "close") {aShadesOff.setLevel(0)}
        else if (aShadesCmdOff == "25") {aShadesOff.setLevel(25)}
        else if (aShadesCmdOff == "50") {aShadesOff.setLevel(50)}
        else if (aShadesCmdOff == "75") {aShadesOff.setLevel(75)}
    }

    // THERMOSTATS
    if (cTstatOff) { thermostatsOff() }
    if (cTstat1Off) { thermostats1Off() }

}    

/***********************************************************************************************************************
    DIMMERS HANDLER - FOR ACTIONS OFF PROCESS
***********************************************************************************************************************/
def dimmersOffHandlerOff() {
	if (logs) log.info "Dimmers Handler activated"
		if (aDimOff) {
            if (aDimCmdOff == "on") {aDimOff.on()}
            else if (aDimCmdOff == "off") {aDimOff.off()}
            if (aDimCmdOff == "set" && aDimOff) {
                def level = aDimLVLOff < 0 || !aDimLVLOff ?  0 : aDimLVLOff >100 ? 100 : aDimLVLOff as int
                    aDimOff.setLevel(level)
            }
            if (aDimCmdOff == "increase" && aDim) {
                def newLevel
                aDimOff?.each {deviceD ->
                    def currLevel = deviceD.latestValue("level")
                    newLevel = aDimIncrease
                    newLevel = newLevel + currLevel
                    newLevel = newLevel < 0 ? 0 : newLevel >100 ? 100 : newLevel
                    deviceD.setLevel(newLevel)
                }
            }
            if (aDimCmdOff == "decrease" && aDimOff) {
                def newLevel
                aDimOff?.each {deviceD ->
                    def currLevel = deviceD.latestValue("level")
                    newLevel = aDimDecrease
                    newLevel = currLevel - newLevel
                    newLevel = newLevel < 0 ? 0 : newLevel >100 ? 100 : newLevel
                    deviceD.setLevel(newLevel)
                }
            }
        }
    }

/***********************************************************************************************************************
    OTHER DIMMERS HANDLER - FOR ACTIONS OFF PROCESS
***********************************************************************************************************************/
def otherDimmersOffHandler() {
	if (logs) log.info "Other Dimmers Handler activated"
        if (aOtherDimOff) {
            if (aOtherDimCmdOff == "on") {aOtherDimOff.on()}
            else if (aOtherDimCmdOff == "off") {aOtherDimOff.off()}
            if (aOtherDimCmdOff == "set" && aOtherDimOff) {
                def otherLevel = aOtherDimLVLOff < 0 || !aOtherDimLVLOff ?  0 : aOtherDimLVLOff >100 ? 100 : aOtherDimLVLOff as int
                    aOtherDimOff?.setLevel(otherLevel)
            }
            if (aOtherDimCmdOff == "increase" && aOtherDimOff) {
                def newLevel
                aOtherDimOff.each { deviceD ->
                    def currLevel = deviceD.latestValue("level")
                    newLevel = aOtherDimIncreaseOff
                    newLevel = newLevel + currLevel
                    newLevel = newLevel < 0 ? 0 : newLevel >100 ? 100 : newLevel
                    deviceD.setLevel(newLevel)
                }
            }
            if (aOtherDimCmdOff == "decrease" && aOtherDimOff) {
                def newLevel
                aOtherDimCmdOff?.each { deviceD ->
                    def currLevel = deviceD.latestValue("level")
                    newLevel = aOtherDimDecreaseOff
                    newLevel = currLevel - newLevel
                    newLevel = newLevel < 0 ? 0 : newLevel >100 ? 100 : newLevel
                    deviceD.setLevel(newLevel)
                }
            }
        }
	}

/************************************************************************************************************
FANS Handler  - FOR ACTIONS OFF PROCESS
************************************************************************************************************/
def aFansOnOff(evt) {
    if (logs) log.info "Fan device handler turn on activated"
    aFansOff?.on()
}
def aFansOffOff(evt) {
    if (logs) log.info "Fan device handler turn off activated"
    aFansOff?.off()
}  

/************************************************************************************************************
THERMOSTATS HANDLERS - FOR ACTIONS OFF PROCESS
************************************************************************************************************/
private thermostatsOff(evt) {
    if (logs) log.info "thermostats handler method activated"
    cTstatOff.each {deviceD ->
        def currentMode = deviceD.currentValue("thermostatMode")
        def currentTMP = deviceD.currentValue("temperature")
        if (cTstatModeOff == "off") { cTstatOff.off()
                                 }
        if (cTstatModeOff == "auto" || cTstatModeOff == "on") {
            cTstatOff.auto()
            cTstatOff.setCoolingSetpoint(coolLvl)
            cTstatOff.setHeatingSetpoint(heatLvl)
        }
        if (cTstatModeOff == "cool") {
            cTstatOff.cool()
            cTstatOff.setCoolingSetpoint(coolLvl)
        }
        if (cTstatModeOff == "heat") {
            cTstatOff.heat()
            cTstatOff.setHeatingSetpoint(heatLvl)
        }
        if (cTstatModeOff == "incr") {
            def cNewSetpoint = tempChange
            cNewSetpoint = tempChange + currentTMP
            cNewSetpoint = cNewSetpoint < 60 ? 60 : cNewSetpoint > 85 ? 85 : cNewSetpoint
            def hNewSetpoint = tempChange
            hNewSetpoint = tempChange + currentTMP
            hNewSetpoint = hNewSetpoint < 60 ? 60 : hNewSetpoint > 85 ? 85 : hNewSetpoint
            if (currentMode == "auto" || currentMode == "on") {
                deviceD.setCoolingSetpoint(cNewSetpoint)
                deviceD.setHeatingSetpoint(hNewSetPoint)
            }
            if (currentMode == "cool") {
                deviceD.setCoolingSetpoint(cNewSetpoint)
            }
            if (currentMode == "heat") {
                deviceD.setHeatingSetpoint(hNewSetPoint)
            }
        }
        if (cTstatModeOff == "decr") {
            def cNewSetpoint = tempChange
            cNewSetpoint = currentTMP - tempChange
            cNewSetpoint = cNewSetpoint < 60 ? 60 : cNewSetpoint > 85 ? 85 : cNewSetpoint
            def hNewSetpoint = tempChange
            hNewSetpoint = currentTMP - tempChange
            hNewSetpoint = hNewSetpoint < 60 ? 60 : hNewSetpoint > 85 ? 85 : hNewSetpoint
            if (currentMode == "auto" || currentMode == "on") {
                deviceD.setCoolingSetpoint(cNewSetpoint)
                deviceD.setHeatingSetpoint(hNewSetPoint)
            }
            if (currentMode == "cool") {
                deviceD.setCoolingSetpoint(cNewSetpoint)
            }
            if (currentMode == "heat") {
                deviceD.setHeatingSetpoint(hNewSetPoint)
            }
        }
        if (cTstatFanOff == "auto" || cTstatFanOff == "off") { cTstatOff.fanAuto() }
        if (cTstatFanOff == "on") { cTstatOff.fanOn() }
        if (cTstatFanOff == "circ") { cTstatOff.fanCirculate() }
    }
}
private thermostats1Off(evt) {
    cTstat1Off.each {deviceD ->
        def currentMode = deviceD.currentValue("thermostatMode")
        def currentTMP = deviceD.currentValue("temperature")
        if (cTstat1ModeOff == "off") { cTstat1Off.off()
                                  }
        if (cTstat1ModeOff == "auto" || cTstat1ModeOff == "on") {
            cTstat1Off.auto()
            cTstat1Off.setCoolingSetpoint(coolLvl1)
            cTstat1Off.setHeatingSetpoint(heatLvl1)
        }
        if (cTstat1ModeOff == "auto" || cTstat1ModeOff == "on") {
            cTstat1Off.auto()
            cTstat1Off.setCoolingSetpoint(coolLvl1)
            cTstat1Off.setHeatingSetpoint(heatLvl1)
        }
        if (cTstat1ModeOff == "incr") {
            def cNewSetpoint = tempChange1
            cNewSetpoint = tempChange1 + currentTMP
            cNewSetpoint = cNewSetpoint < 60 ? 60 : cNewSetpoint > 85 ? 85 : cNewSetpoint
            def hNewSetpoint = tempChange1
            hNewSetpoint = tempChange1 + currentTMP
            hNewSetpoint = hNewSetpoint < 60 ? 60 : hNewSetpoint > 85 ? 85 : hNewSetpoint
            if (currentMode == "auto" || currentMode == "on") {
                deviceD.setCoolingSetpoint(cNewSetpoint)
                deviceD.setHeatingSetpoint(hNewSetPoint)
            }
            if (currentMode == "cool") {
                deviceD.setCoolingSetpoint(cNewSetpoint)
            }
            if (currentMode == "heat") {
                deviceD.setHeatingSetpoint(hNewSetPoint)
            }
        }
        if (cTsta1tModeOff == "decr") {
            def cNewSetpoint = tempChange1
            cNewSetpoint = currentTMP - tempChange1
            cNewSetpoint = cNewSetpoint < 60 ? 60 : cNewSetpoint > 85 ? 85 : cNewSetpoint
            def hNewSetpoint = tempChange1
            hNewSetpoint = currentTMP - tempChange1
            hNewSetpoint = hNewSetpoint < 60 ? 60 : hNewSetpoint > 85 ? 85 : hNewSetpoint
            if (currentMode == "auto" || currentMode == "on") {
                deviceD.setCoolingSetpoint(cNewSetpoint)
                deviceD.setHeatingSetpoint(hNewSetPoint)
            }
            if (currentMode == "cool") {
                deviceD.setCoolingSetpoint(cNewSetpoint)
            }
            if (currentMode == "heat") {
                deviceD.setHeatingSetpoint(hNewSetPoint)
            }
        }
        if (cTstat1FanOff == "auto" || cTstat1FanOff == "off") { cTstat1Off.fanAuto() }
        if (cTstat1FanOff == "on") { cTstat1Off.fanOn() }
        if (cTstat1FanOff == "circ") { cTstat1Off.fanCirculate() }
    }
}


/******************************************************************************************************
	CONDITIONS - CERTAIN TIME RESTRICTION
******************************************************************************************************/
page name: "certainTime"
def certainTime() {
    dynamicPage(name:"certainTime",title: "", uninstall: false) {
        section("") {
            input "startingX", "enum", title: "Starting at...", options: ["A specific time", "Sunrise", "Sunset"], required: false , submitOnChange: true
            if(startingX in [null, "A specific time"]) input "starting", "time", title: "Start time", required: false, submitOnChange: true
            else {
                if(startingX == "Sunrise") input "startSunriseOffset", "number", range: "*..*", title: "Offset in minutes (+/-)", required: false, submitOnChange: true
                else if(startingX == "Sunset") input "startSunsetOffset", "number", range: "*..*", title: "Offset in minutes (+/-)", required: false, submitOnChange: true
                    }
        }
        section("") {
            input "endingX", "enum", title: "Ending at...", options: ["A specific time", "Sunrise", "Sunset"], required: false, submitOnChange: true
            if(endingX in [null, "A specific time"]) input "ending", "time", title: "End time", required: false, submitOnChange: true
            else {
                if(endingX == "Sunrise") input "endSunriseOffset", "number", range: "*..*", title: "Offset in minutes (+/-)", required: false, submitOnChange: true
                else if(endingX == "Sunset") input "endSunsetOffset", "number", range: "*..*", title: "Offset in minutes (+/-)", required: false, submitOnChange: true
                    }
        }
    }
}

// TIME RESTRICTIONS - ENTIRE ROUTINE
private getTimeOk(evt) {
    def result = true
    if ((starting && ending) ||
        (starting && endingX in ["Sunrise", "Sunset"]) ||
        (startingX in ["Sunrise", "Sunset"] && ending) ||
        (startingX in ["Sunrise", "Sunset"] && endingX in ["Sunrise", "Sunset"])) {
        def currTime = now()
        def start = null
        def stop = null
        def s = getSunriseAndSunset(zipCode: zipCode, sunriseOffset: startSunriseOffset, sunsetOffset: startSunsetOffset)
        if(startingX == "Sunrise") start = s.sunrise.time
        else if(startingX == "Sunset") start = s.sunset.time
            else if(starting) start = timeToday(starting,location.timeZone).time
                s = getSunriseAndSunset(zipCode: zipCode, sunriseOffset: endSunriseOffset, sunsetOffset: endSunsetOffset)
            if(endingX == "Sunrise") stop = s.sunrise.time
            else if(endingX == "Sunset") stop = s.sunset.time
                else if(ending) stop = timeToday(ending,location.timeZone).time
                    result = start < stop ? currTime >= start && currTime <= stop : currTime <= stop || currTime >= start
            }
    if(logging) log.trace "timeOk = $result"
    return result
}
private hhmm(time, fmt = "h:mm a") {
    def t = timeToday(time, location.timeZone)
    def f = new java.text.SimpleDateFormat(fmt)
    f.setTimeZone(location.timeZone ?: timeZone(time))
    f.format(t)
}
private offset(value) {
    def result = value ? ((value > 0 ? "+" : "") + value + " min") : ""
}
private timeIntervalLabel() {
    def result = "complete"
    if      (startingX == "Sunrise" && endingX == "Sunrise") result = "Sunrise" + offset(startSunriseOffset) + " to Sunrise" + offset(endSunriseOffset)
    else if (startingX == "Sunrise" && endingX == "Sunset") result = "Sunrise" + offset(startSunriseOffset) + " to Sunset" + offset(endSunsetOffset)
        else if (startingX == "Sunset" && endingX == "Sunrise") result = "Sunset" + offset(startSunsetOffset) + " to Sunrise" + offset(endSunriseOffset)
            else if (startingX == "Sunset" && endingX == "Sunset") result = "Sunset" + offset(startSunsetOffset) + " to Sunset" + offset(endSunsetOffset)
                else if (startingX == "Sunrise" && ending) result = "Sunrise" + offset(startSunriseOffset) + " to " + hhmm(ending, "h:mm a z")
                    else if (startingX == "Sunset" && ending) result = "Sunset" + offset(startSunsetOffset) + " to " + hhmm(ending, "h:mm a z")
                        else if (starting && endingX == "Sunrise") result = hhmm(starting) + " to Sunrise" + offset(endSunriseOffset)
                            else if (starting && endingX == "Sunset") result = hhmm(starting) + " to Sunset" + offset(endSunsetOffset)
                                else if (starting && ending) result = hhmm(starting) + " to " + hhmm(ending, "h:mm a z")
                                    }

/******************************************************************************************************
SPEECH AND TEXT ACTION
******************************************************************************************************/
def ttsActions(msg) {
    log.info "TTS Actions Handler activated"
    def tts = msg
    if (echoDevice) {
        log.info "echoDevice: $echoDevice activated"
        echoDevice?.speak(tts)
    }
    if (synthDevice) {
        synthDevice?.speak(tts) 
    }
    if (tts) {
        state.sound = textToSpeech(tts instanceof List ? tts[9] : tts)
    }
    else {
        state.sound = textToSpeech("You selected the custom message option but did not enter a message in the $app.label Smart App")
    }
    if (sonosDevice){ 
        def currVolLevel = sonosDevice.latestValue("level")
        def currMuteOn = sonosDevice.latestValue("mute").contains("muted")
        if (currMuteOn) { 
            sonosDevice.unmute()
        }
        def sVolume = settings.volume ?: 20
        sonosDevice?.playTrackAndResume(state.sound.uri, state.sound.duration, sVolume)
    }
    if(recipients || sms){				
        sendtxt(tts)
    }
    if (push) {
        sendPushMessage(tts)
    }	
    state.lastMessage = tts
    return
}

/***********************************************************************************************************************
	SMS HANDLER
***********************************************************************************************************************/
private void sendtxt(tts) {
    if (logging) log.info "Send Text method activated."
    if (sendContactText) { 
        sendNotificationToContacts(tts, recipients)
        if (push || shmNotification) { 
            sendPushMessage
        }
    } 
    if (notify) {
        sendNotificationEvent(tts)
    }
    if (sms) {
        sendText(sms, tts)
    }
    if (psms) {
        processpsms(psms, tts)
    }
}

private void sendText(number, tts) {
    if (sms) {
        def phones = sms.split("\\,")
        for (phone in phones) {
            sendSms(phone, tts)
        }
    }
}
private void processpsms(psms, tts) {
    if (psms) {
        def phones = psms.split("\\,")
        for (phone in phones) {
            sendSms(phone, tts)
        }
    }
}

/******************************************************************************************************
MAIN PAGE STATUS CHECKS
******************************************************************************************************/
// SETTINGS PAGE
def settingsPageSettings() {
    def result = "Tap here to Configure"
    if (logs || safety) {
        result = "complete"
    }
    return result
}
def settingsPageComplete() {
    def result = "Tap here to Configure"
    if (logs || safety) {
        result = "Configured!"
    }
    return result
}  

// CONDITIONS PAGE
def condPageSettings() {
    def result = "Tap here to Configure"
    if (cMode || cDays) {
        result = "complete"
    }
    return result
}
def condPageComplete() {
    def result = "Tap here to Configure"
    if (cMode || cDays) {
        result = "Conditions have been Configured!"
    }
    return result
}  

// CONDITIONS FAIL PAGE
def condFailPageSettings() {
    def result = "Tap here to Configure"
    if (blank) {
        result = "complete"
    }
    return result
}
def condFailPageComplete() {
    def result = "Tap here to Configure"
    if (blank) {
        result = "Configured!"
    }
    return result
}  


// ACTIONS PAGE
def actionsPageSettings() {
    def result = "Tap here to Configure"
    if (actionsOnPageSettings() == "complete" || actionsOffPageSettings() == "complete" ) {
        result = "complete"
    }
    return result
}
def actionsPageComplete() {
    def result = "Tap here to Configure"
    if (actionsOnPageSettings() == "complete" || actionsOffPageSettings() == "complete" ) {
        return "Actions have been Configured!"
        result = true
    }
    return result
}  

// ACTIONS ON PAGE
def actionsOnPageSettings() {
    def result = "Tap here to Configure"
    if (blank ) {
        result = "complete"
    }
    return result
}
def actionsOnPageComplete() {
    def result = "Tap here to Configure"
    if (blank ) {
        return "On Actions have been Configured!"
        result = true
    }
    return result
}  

// ACTIONS OFF PAGE
def actionsOffPageSettings() {
    def result = "Tap here to Configure"
    if (blank ) {
        result = "complete"
    }
    return result
}
def actionsOffPageComplete() {
    def result = "Tap here to Configure"
    if (blank ) {
        return "Off Actions have been Configured!"
        result = true
    }
    return result
}
// TIME RESTRICTIONS
def pTimeSettings(){ def result = "" 
                    if (startingX || endingX) { 
                        result = "complete"}
                    result}
def pTimeComplete() {def text = "Tap here to Configure" 
                     if (startingX || endingX) {
                         text = "Configured"}
                     else text = "Tap here to Configure"
                     text}



