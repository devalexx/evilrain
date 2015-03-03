dynActorArray = {}

function onCreate(world)
    world:setWinHint('Drop houses!')

    local ground = luajava.new(Ground)
    ground:addVertex(752, 48)
    ground:addVertex(752, 344)
    ground:addVertex(696, 208)
    ground:addVertex(600, 208)
    ground:addVertex(504, 136)
    ground:addVertex(96, 136)
    ground:addVertex(32, 40)
    ground:addVertex(32, 32)
    world:add(ground)

    local dynamicActor = luajava.new(Home)
    world:add(dynamicActor)
    dynamicActor:setPosition(luajava.new(Vector2, 650, 300))
    table.insert(dynActorArray, dynamicActor)

    for i = 0, 3 do
        local dynamicActorTmp = luajava.new(Home)
        dynamicActorTmp:setBodyBox(20, 100 + i * 10)
        dynamicActorTmp:setSpriteBox(20, 100 + i * 10)
        world:add(dynamicActorTmp)
        dynamicActorTmp:setPosition(luajava.new(Vector2, 150 + i * 90, 200))

        table.insert(dynActorArray, dynamicActorTmp)
    end

    local cloud = luajava.new(Cloud)
    cloud:setPosition(luajava.new(Vector2, 50, 420))
    world:add(cloud)
end

function onCheck()
    for i = 1, #dynActorArray do
        if dynActorArray[i]:getRotation() > -30 and dynActorArray[i]:getRotation() < 30 and
                dynActorArray[i]:getPosition().y > 0 then
            return false
        end
    end

    return true
end