-- some function adjustments to get parity with lua 5.2

-- pairs override
local oldPairs=pairs
pairs=function(tbl)
    if getmetatable(tbl)~=nil and getmetatable(tbl).__pairs~=nil then
        return getmetatable(tbl).__pairs(tbl)
    end 
    return oldPairs(tbl)
end

-- ipairs override
local oldiPairs=ipairs
ipairs=function(tbl)
    if getmetatable(tbl)~=nil and getmetatable(tbl).__ipairs~=nil then
        return getmetatable(tbl).__ipairs(tbl)
    end 
    return oldiPairs(tbl)
end
