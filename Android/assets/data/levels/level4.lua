dynActorArray = {}
isContacted = false

function onCreate()
    world:setPressingAction(DRAWING);

    local ground = luajava.new(Ground)
    ground:addVertex(752, 48)
    ground:addVertex(752, 344)
    ground:addVertex(696, 208)
    ground:addVertex(600, 208)
    ground:addVertex(504, 136)
    ground:addVertex(96, 136)
    ground:addVertex(32, 360)
    ground:addVertex(32, 32)
    world:add(ground)

    dynamicActor = luajava.new(Hammer)
    world:add(dynamicActor)
    dynamicActor:setPosition(luajava.new(Vector2, 620, 280))
    table.insert(dynActorArray, dynamicActor)

    dynamicActorTmp = luajava.new(House)
    dynamicActorTmp:setBodyBox(20, 100)
    dynamicActorTmp:setSpriteBox(20, 100)
    world:add(dynamicActorTmp)
    dynamicActorTmp:setPosition(luajava.new(Vector2, 460, 190))

    table.insert(dynActorArray, dynamicActorTmp)

    world:addDrawingZone(0, 350, 800, 150)
end

function onCheck()
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