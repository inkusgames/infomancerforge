
-- Create basic get and set methods for java objects
local function createObjectGetSet(property)
    property=(property:gsub("^%l", string.upper))
    local get="get" .. property
    local is="is" .. property
    local set="get" .. property
    return {
        get=function(obj)
            if obj[get]~=nil then
            	return obj[get](obj)
            elseif obj[is]~=nil then
            	return obj[is](obj)
            end
        end,
        set=function(obj,value)
            obj[set](obj,value)
        end
    }
end

-- Create a lua table that represents a object
function buildObjLuaTable(object,fields,meta,currentTable)
    local _data={}

    local data=currentTable or {}

    for _,k in ipairs(fields) do
        _data[k]=createObjectGetSet(k)
    end
    for k,v in pairs(fields) do
        _data[k]=v
    end

    local _meta=meta or {} 
 
    _meta.__index = function(tbl, k)
        if type(k)=="number" then
            if _meta.__arrayItem~=nil then
                return _meta.__arrayItem(tbl,k)
            else
                return _data[k]~=nil
             
            end
        else
            if _data[k]~=nil then
                return _data[k].get(object)
            end
        end
    end

    _meta.__newindex = function(_,k,v)
        if _data[k]~=nil and _data[k].set~=nil then
            return _data[k].set(object,v)
        end
    end
 
    _meta.__pairs = function(_)
        local pos=1;

        local function stateless_iter(tbl, _)
            local k=nil
            local v=nil
            while pos<=#fields and v==nil do
                k=fields[pos]
                v=tbl[k]
                pos=pos+1
                if v==nil then 
                    k=nil 
                end
            end 

            return k,v
        end

        -- Return an iterator function, the table, starting point
        return stateless_iter, data, nil
    end

    setmetatable(data,_meta)

    return data
end

-- Bind the enum of GOBPropertyDefinition.Type
local gobPropertyTypes = luajava.bindClass("com.cinch.adventurebuilderstoolkit.beans.gobs.GOBPropertyDefinition$Type")

-- List of fields available in a gob record
local gobPropertyDefinitionFields={
    "name",
    "array",
    "required",
    "minInt",
    "maxInt",
    "minFloat",
    "maxFloat",
    "gobType",
    "gobFieldName",
    "propertyType",
    propertyType={
        get=function(obj)
            return obj:getType():name()
        end,
        set=function(obj,value)
            obj:setType(gobPropertyTypes.valueOf(value))
        end
    }
}

-- Create a lua table that represents a gob
function buildPropertyDefinition(p)
    return buildObjLuaTable(p,gobPropertyDefinitionFields)
end

-- Bind the enum of GOB.Type
local gobTypes = luajava.bindClass("com.cinch.adventurebuilderstoolkit.beans.gobs.GOB$Type")

-- List of fields available in a gob record
local gobFields={
    "uuid",
    "name",
    "parent",
    "summary",
    "colorBackground",
    "gobType",
    "propertyDefinitions",
    "data",
    colorBackground={
        get=function(obj)
            local c=obj:getColorBackground() 
            if c~=nil then
	           return string.format("#%02x%02x%02x", obj:getColorBackground():getRed(), obj:getColorBackground():getGreen() , obj:getColorBackground():getBlue())
	       end
        end
    },
    gobType={
        get=function(obj)
            return obj:getType():name()
        end,
        set=function(obj,value)
            obj:setType(gobTypes.valueOf(value))
        end
    },
    -- TODO: Find a better way to handle this so new values can be added and values can be deleted
    propertyDefinitions={
        get=function(obj)
            local fields={}
            local pdList=obj:getPropertyDefinitions()
            if pdList:size()~=0 then  
                for t=0,pdList:size()-1 do
                    fields[#fields+1]=buildPropertyDefinition(pdList:get(t))
                end
            end
            return fields
        end
    }
}

-- Create a lua table that represents a gob
function buildGob(gob)
    local uuid=gob:getUuid()
    local data={}
    setmetatable(data,{
	    __ipairs = function(tbl)
            local _all={}
            local _i=environment:getGobInstacesByUuid(uuid) or {}
            for _,v in ipairs(_i) do
                _all[#_all+1]=buildInstance(v)
            end
	        return ipairs(_all)
        end,
        
        __len = function(tbl)
            return #environment:getGobInstacesByUuid(uuid)
        end,
        
        __arrayItem = function(tbl,k)
            return buildInstance(environment:getGobInstacesByUuid(uuid)[k])
        end
    })

    return buildObjLuaTable(gob,gobFields,nil,{
        data=data
    })

    -- return buildObjLuaTable(gob,gobFields,{
	--     __ipairs = function(tbl)
    --         local _all={}
    --         local _i=environment:getGobInstacesByUuid(tbl.uuid) or {}
    --         for _,v in ipairs(_i) do
    --             _all[#_all+1]=buildInstance(v)
    --         end
	--         return ipairs(_all)
    --     end,
        
    --     __len = function(tbl)
    --         return #environment:getGobInstacesByUuid(tbl.uuid)
    --     end,
        
    --     __arrayItem = function(tbl,k)
    --         return buildInstance(environment:getGobInstacesByUuid(tbl.uuid)[k])
    --     end
    -- })
end
 
-- Build an instance that will point to provided gobInstance lua interface
function buildInstance(instance)
    local data={
    }

    setmetatable(data,{
        __index = function(_, k)
            if type(k)=="string" then
				-- print("Getting value from:" .. k)
				-- if instance==nil then
				-- 	print("Instance is nil")
				-- else 
				-- 	print("Instance is not nil")
				-- end
				-- if instance.getFieldValue==nil then
				-- 	print("Instance.getFieldValue is nil")
				-- 	for nn,_ in pairs(instance) do
				-- 		print("Instance has field '" .. nn .. "'")
				-- 	end
				-- end
            	return instance:getFieldValue(k)
            end
        end,

        __newindex = function(_,k,v)
            instance:setFieldValue(k,v)
        end,

        __pairs = function(_)
            local keys=instance:getFields()
            local pos=1;

            local function stateless_iter(tbl, _)
                local k=nil
                local v=nil
 
                while pos<=#keys and v==nil do 
                    k=keys[pos]
                    v=instance:getFieldValue(k)
                    pos=pos+1
                    if v==nil then 
                        k=nil 
                    end
	           end

                return k,v
            end

            -- Return an iterator function, the table, starting point
            return stateless_iter, data, nil
        end
    })

    return data
end

local _gobs={
}

gobs={}
setmetatable(gobs,{
    __index = function(_, k)
        if _gobs[k]==nil and type(k)=="string" then
            local _g=environment:getGobByName(k)
            if _g == nil then
                print("Unable to find GOB('" .. k .. "')")
            else
                _gobs[k]=buildGob(_g)
            end
        end
        return _gobs[k]
    end,

    __newindex = function(_,k,v)
        --TODO: Can we create new gobs like this? Don't know how we would choose location on the drive. Perhaps these gobs are only available in views?
    end,

    __pairs = function(tbl)
        local keys=environment:getAllGobNames()
        local pos=1;

        local function stateless_iter(tbl, _)
            local k=nil
            local v=nil
            if pos<=#keys then
                k=keys[pos]
                v=gobs[k]
                pos=pos+1
            end

            return k,v
        end

        -- Return an iterator function, the table, starting point
        return stateless_iter, tbl, nil
    end
})
