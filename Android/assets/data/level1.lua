dynActorArray = {}

function onCreate(world)
    world:setWinHint('Drop houses!')

    ground = luajava.new(Ground)
    ground:addVertex(752, 48)
    ground:addVertex(752, 344)
    ground:addVertex(696, 208)
    ground:addVertex(600, 208)
    ground:addVertex(504, 136)
    ground:addVertex(96, 136)
    ground:addVertex(32, 360)
    ground:addVertex(32, 32)
    world:add(ground)

    dynamicActor = luajava.new(Home)
    world:add(dynamicActor)
    dynamicActor:setPosition(luajava.new(Vector2, 650, 300))
    table.insert(dynActorArray, dynamicActor)

    for i = 0, 3 do
        dynamicActorTmp = luajava.new(Home)
        dynamicActorTmp:setBodyBox(20, 100 + i * 10)
        dynamicActorTmp:setSpriteBox(20, 100 + i * 10)
        world:add(dynamicActorTmp)
        dynamicActorTmp:setPosition(luajava.new(Vector2, 150 + i * 90, 200))

        table.insert(dynActorArray, dynamicActorTmp)
    end

    cloud = luajava.new(Cloud)
    world:add(cloud)
    cloud:setPosition(luajava.new(Vector2, 650, 420))
end

function onCheck(mArray)
    for i = 1, #dynActorArray do
        if dynActorArray[i]:getRotation() > -30 and dynActorArray[i]:getRotation() < 30 then
            return false
        end
    end

    return true
end