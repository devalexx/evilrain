dynActorArray = {}

function onCreate(world)
    world:setWinHint('Drop houses!')

    ground = luajava.newInstance(Ground)
    ground:addVertex(752, 48)
    ground:addVertex(752, 344)
    ground:addVertex(696, 208)
    ground:addVertex(600, 208)
    ground:addVertex(504, 136)
    ground:addVertex(272, 152)
    ground:addVertex(96, 136)
    ground:addVertex(32, 360)
    ground:addVertex(32, 32)
    world:add(ground)

    dynamicActor = luajava.newInstance(DynamicActor)
    world:add(dynamicActor)
    dynamicActor:setPosition(luajava.newInstance(Vector2, 650, 300))
    table.insert(dynActorArray, dynamicActor)

    for i = 0, 5 do
        dynamicActorTmp = luajava.newInstance(DynamicActor)
        dynamicActorTmp:setBodyBox(20, 50 + i * 10)
        dynamicActorTmp:setSpriteBox(20, 50 + i * 10)
        world:add(dynamicActorTmp)
        dynamicActorTmp:setPosition(luajava.newInstance(Vector2, 150 + i * 50, 200))

        table.insert(dynActorArray, dynamicActorTmp)
    end

    cloud = luajava.newInstance(Cloud)
    world:add(cloud)
    cloud:setPosition(luajava.newInstance(Vector2, 650, 420))
end

function onCheck(mArray)
    for i = 1, #dynActorArray do
        if dynActorArray[i]:getRotation() > -30 and dynActorArray[i]:getRotation() < 30 then
            return false
        end
    end

    return true
end