dynActorArray = {}
isContacted = false

function onCreate(world)
    world:setWinHint('Use hammer!')

    ground = luajava.newInstance(Ground)
    ground:addVertex(752, 48)
    ground:addVertex(752, 344)
    ground:addVertex(696, 208)
    ground:addVertex(600, 208)
    ground:addVertex(504, 136)
    ground:addVertex(96, 136)
    ground:addVertex(32, 360)
    ground:addVertex(32, 32)
    world:add(ground)

    dynamicActor = luajava.newInstance(Hammer)
    world:add(dynamicActor)
    dynamicActor:setPosition(luajava.newInstance(Vector2, 600, 300))
    table.insert(dynActorArray, dynamicActor)

    dynamicActorTmp = luajava.newInstance(Home)
    dynamicActorTmp:setBodyBox(20, 100)
    dynamicActorTmp:setSpriteBox(20, 100)
    world:add(dynamicActorTmp)
    dynamicActorTmp:setPosition(luajava.newInstance(Vector2, 450, 200))

    table.insert(dynActorArray, dynamicActorTmp)
end

function onCheck(mArray)
    return isContacted
end

function onBeginContact(contact)
    if contact:getFixtureA():getBody() == dynamicActorTmp:getBody() and
            contact:getFixtureB():getBody() == dynamicActor:getBody()
            or
            contact:getFixtureB():getBody() == dynamicActorTmp:getBody() and
            contact:getFixtureA():getBody() == dynamicActor:getBody() then
        isContacted = true
    end
end