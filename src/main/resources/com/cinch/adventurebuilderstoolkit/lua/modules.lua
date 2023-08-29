
-- Replaces the standard require funciton with one that will load the module from the environment
function require(name)
    return environment:getModule(name)
end