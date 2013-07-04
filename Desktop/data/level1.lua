dofile "data/main.lua"

function onCreate(world)
    drop = luajava.newInstance(Drop)
    world:add(drop)
    drop:setPosition(luajava.newInstance(Vector2, 100, 400))

    for i = 0, 19 do
        for j = 0, 19 do
            drop = luajava.newInstance(Drop)
            world:add(drop)
            drop:setPosition(luajava.newInstance(Vector2, i * 15 + 130, j * 15 + 140))
        end
    end

    ground = luajava.newInstance(Ground)
    ground:addVertex(752, 48)
    ground:addVertex(752, 344)
    ground:addVertex(696, 208)
    ground:addVertex(600, 208)
    ground:addVertex(552, 344)
    ground:addVertex(504, 136)
    ground:addVertex(272, 152)
    ground:addVertex(96, 136)
    ground:addVertex(32, 360)
    ground:addVertex(32, 32)
    world:add(ground)

    dynamicActor = luajava.newInstance(DynamicActor)
    world:add(dynamicActor)
    dynamicActor:setPosition(luajava.newInstance(Vector2, 650, 300))
end

function onCheck(mArray)
    if dynamicActor:getRotation() < -10 then
        return true
    end

    return false
end