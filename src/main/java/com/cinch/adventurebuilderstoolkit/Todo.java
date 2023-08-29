package com.cinch.adventurebuilderstoolkit;

//TODO: [x] Add in HSQL hide the DB in a .data folder.
//TODO: [x] Hide the .data folder
//TODO: [x] Add a field to each gob for it's tables name
//TODO: [x] Add a field to each gob property for it's fields name
//TODO: [x] Make sure new gob files get a suitable table name
//TODO: [x] Make sure gob properties get a suitable field name
//TODO: [x] Create a system to update a gob's table to match it's new design.
//TODO: 	[x] Update table design on every save of the gob instance
//TODO: [x] Make sure GOB don't flag as modified when they are not
//TODO: [x] Parent tree nodes should be marked as changed when a child has changed so they repaint
//TODO: [x] Stop GOB editor from incorrectly flagging gob as changed when open and when an edit didn't change anything
//TODO: [x] Make ID value for base class of base type.
//TODO: [x] When creating a GOB field we need to include parents attributes (Exclude any id fields other the the parents.
//TODO: [x] Make table editing for gob properties and event list table model so that when the list changes the model will update automatically

//TODO: [x] When a gob has a parent it can not have an ID since that would have to come from the parent.
//TODO: [x] Make sure when updating a gob it can identify all it's parents.
//TODO: [x] Changing the properties of a parent should also mark the children as changed

//TODO: [x] Make GOB Parents point to the GOB uuid not to the name

//TODO: [x] Give gobs a property for if they can have Data or not. Meaning that they dont have a table or the ability to capture data. They are just a base.
//TODO: [x] Gob instances need a boolean for new or loaded

//TODO: [x] Make GOB Instances a type of Data Instance.

//TODO: [x] Rename file when a gob changes it's name
//TODO: [x] Make sure tree re-renders correctly on gob change
//TODO: [x] Rename tabs when a gob changes name
//TODO: [x] Make sure gob editors open the correct one after a name change

//TODO: [x] Gob Designer properties handle GOB's
//TODO: [x] Gob Designer allow editing of the ID field name

//TODO: [x] Gob Instance add row
//TODO: [x] Gob Instance edit row
//TODO: [x] Gob Instance delete row
//TODO: [x] Gob Save Data.
//TODO: [x] Gob Instance edit link to other GOB instances (Not when embedded, when embedded an edit screen needs to pop up from a properties field) Must show the type and any children from that type.
//TODO: [x] Make sure labels reset on fields with no changes
//TODO: [x] Make change number label not use an icon but draw a circle box under the changes
//TODO: [x] GOB Instances need to be able to save/load
//TODO: [x] Gob Instance changes update counters when saving
//TODO: [x] Gob Instance changes update tree when changes are made
//TODO: [x] Gob instances source model. Needs a list of loaded gobs, changed or otherwise. Could use a cache object under the hood. From that filtered lists that allow narrow views of the results. All objects that have been modified need to be preserved. 
//TODO: [x] GOB Instances can not be modified until after a changed gob has been saved. (Assuming the change modifies the database)

//TODO: [x] Add button for Design Views
//TODO: [x] Create Design View
//TODO: [x] Save Design View
//TODO: [x] DesignView: Add New GOB
//TODO: [x] DesignView: Link to current GOB
//TODO: [x] DesignView: Scroll around view
//TODO: [x] DesignView: Zoom view
//TODO: [x] DesignView: Draw Design view grid
//TODO: [x] DesignView: Display Ref saved with view

//TODO: [x] Snap to Grid toggle
//TODO: [x] Show Grid toggle
//TODO: [x] Zoom in
//TODO: [x] Zoom out
//TODO: [x] Reset Zoom
//TODO: [x] Choose Grid Type

//TODO: [x] Allow selecting of Gob Instances
//TODO: [x] Allow moving of Gob Instances
//TODO: [x] Create change event listener system for data objects
//TODO: [x] Not all bog instances are named resources. Allow named reesource to deny that it is named

//TODO: [ ] How do we adjust existing GOBInstances properties when a parent has a property added?

//TODO: [x] Make view lines curvy. 
//TODO: [x] Update a view when it is opened as data may have changed when it was closed.
//TODO: [x] When opening a view don't touch and require a save
//TODO: [x] When connections are made on a view fire the dataInstace change
//TODO: [x] Make connector line start at edge of circle and and at edge of the arrow 
//TODO: [x] Dragging for a single gob instance to a location on screen show show a create or link menu

//TODO: [x] New and Link dialog instances can not be of none base types in view edits
//TODO: [x] Embedded Gob's don't get a name also if only one type auto make them
//TODO: [x] Show crow feet for connector not arrow

//TODO: [x] Embedded array connectors

//TODO: [x] Drag from single gob link to a create menu if there are multiple sub types
//TODO: [x] For unnamed tagged views display the first string value in the GOBInstance
//TODO: [x] Edit connector colours
//TODO: [x] View:Stop relinking of GOB's that are already connected to the source.
//TODO: [x] View:Stop relinking of GOB's that are already in the view
//TODO: [x] For embedded data remove add and delete buttons. This kind of data should really only be edited in the Views
//TODO: [x] For embedded data editing add buttons to perform clean up of orphaned data.
//TODO: [x] View:Connector for missing items display in black other colour when it's an array

//TODO: [x] View:When connectors are added try find a more suitable location around the items for the destination arrow.
//TODO: [x] Gobs can only have compatable parents. Base can not become embedded but embedded can become base.
//TODO: [x] Don't allow definition only gobs from having data or being used in views.
//TODO: [>] View:Right click menu to hide all or show all connected items.
//TODO: [>] Solve looped update with notice when editing extra options 
//TODO: [-] Delete GOBinstanced from view. Remove links if embedded delete source, if not embedded ask for data delete or just from view.
//TODO: [x] View:Allow multiple selections
//TODO: [x] View:Properties editor hide/show
//TODO: [x] View:Properties editor for selected Gob(s)
//TODO: [x] View:Properties editor update other displays
//TODO: [>] GOB Instance array of gobs allow for nulls since we have a way to edit the array in the properties view
//TODO: [>] GOB Instance array in properties editor
//TODO: [x] View update summary need to recalc when editing

//TODO: [x] If a GOB is definition only it may not appear in a data editor or view.

//TODO: [x] Allow the adding of the gob instance(s) in view from the connector

//TODO: [x] GOBInstance table model remove array items
//TODO: [x] Show the number of changed records in the tree view when editing data
//TODO: [x] GOB Instance table editing you can not include Embeded GOB instances if they are not named.
//TODO: [x] Gob edit give columns a reasonable size
//TODO: [>] Hot key to add a row when editing
//TODO: [>] Hot key to delete a row when editing
//TODO: [x] Show properties side panel when editing GOBInstances
//TODO: [x] Embedded Gob Views can not be seen as Tag since they have no name.
//TODO: [x] Embedded Gob Views Remove heading from all displays and calculations

//TODO: [>] GOBProperties give each property the ability to be a different color. Then fill the display area for that as that color
//TODO: [x] GOBView add a horizontal seperator for the heading,summary and the fields.
//TODO: [x] GOBView add a verticle seperator for the field names and values

//TODO: [>] Converting integer to array integer does not work, we need to make this work both directions
//TODO: [>] Converting double to array double does not work, we need to make this work both directions
//TODO: [>] Converting string to array string does not work, we need to make this work both directions

//TODO: [>] When editing a GOB update properties if the type of a field has been changed.
//TODO: [>] When editing a GOB update the data editors model when you change the gob's design.
//TODO: [x] When you edit a GOB instance mark the gob as changed.
//TODO: [-] GOB editing view table with properties for nested gobs and arrays.
//TODO: [-] GOB editing view list with full page for editing. Created from basic rules (How flexible should we make this?)
//TODO: [x] GOB Display editor, set the basic paramaters for a GOB to display in the visual editors. (Make this use a type of markdown that can use LUA to modify values fro display)
//TODO: [-] ?? Give gobs a final param, this means they can not be the parent of any further gobs. this is handy for GOBs that are just lists types.

//TODO: [x] Allow getting Gob Instance menu
//TODO: [x] Allow getting Gob Instance menu set size of all of type (or just this one)

//TODO: [x] Gob Instances Render TAG
//TODO: [x] Gob Instances Render Summary
//TODO: [x] Gob Instances Render Fields
//TODO: [x] Gob Instances Render Full

//TODO: [x] Gob property definitions add a boolean for show in table editor
//TODO: [x] When editing data allow a colum width to be defined in the Property and use that in the table. Then allow the table to scroll left and right
//TODO: [x] Make sure GOB table follows cursor if you moving left and right
//TODO: [>] If we adjust a table view colum order adjust the GOB properties to match
//TODO: [>] When a gob has been edited recreate the gob table.
//TODO: [-] When a gob field order has been changed in the table view adjust the gob definition to match that order
//TODO: [>] Gob floats should have a require editing precision
//TODO: [-] Gob Instance Columns should have there own color to render text with.
//TODO: [>] Gob Instance Editor show hide properties editor same way as we do in views.
//TODO: [x] Tables add a row color banding for every 3 rows
//TODO: [>] Gob instance editing, Unset when exiting a value
//TODO: [>] Gob instance editing, Checkbox appears to flip incorrectly on first click
//TODO: [>] Gob Instance Table headers.
//TODO: 	[>] Allow a column name for editing and don't only use the fields name
//TODO: 	[x] Left align header unless the value is a number
//TODO: 	[x] Add the break lines into the tbales (All of them) (Was done with grid colours)
//TODO: 	[>] Can we double click a col break to make it auto size the column
//TODO: 	[>] Number value should render with a fixed width font
//TODO: 	[>] Floats should allow a precision for display

//TODO: [>] Lua functions to add menu items. Must have a key tied to script to when script runs those menu items are reset
//TODO: [>] Lua functions to add config data to system. Must then load and save and be updated if the user edits the config information
//TODO: [>] Config editing tool

//TODO: [>] When changing grid draw/dont draw trigger repaint
//TODO: [-] Add a suitable font for numbers. (Use in all displays and editing)
//TODO: [-] When editing properties show array top and values in a different color to help isolate array items.
//TODO: [x] Gob VIEW make sure full content is centered vertically. (This is off as the size is fixed in sets of 20)
