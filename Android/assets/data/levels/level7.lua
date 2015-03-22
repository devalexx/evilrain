function onCreate(world)
    world:setPressingAction(3);

    local ground = luajava.new(Ground)
    ground:addVertex(792, 0)
    ground:addVertex(792, 336)
    ground:addVertex(712, 320)
    ground:addVertex(624, 288)
    ground:addVertex(536, 248)
    ground:addVertex(432, 200)
    ground:addVertex(312, 160)
    ground:addVertex(168, 96)
    ground:addVertex(0, 0)
    world:add(ground)

    local ground = luajava.new(Ground)
    ground:addVertex(0, 50)
    ground:addVertex(56, 60)
    ground:addVertex(112, 100)
    ground:addVertex(56, 200)
    ground:addVertex(0, 220)
    world:add(ground)

    local ground = luajava.new(Ground)
    ground:addVertex(192, 184)
    ground:addVertex(650, 450)
    ground:addVertex(152, 272)
    ground:addVertex(50, 344)
    world:add(ground)

    local emitterActor = luajava.new(Emitter)
    emitterActor:setHasControl(false)
    world:add(emitterActor)
    emitterActor:setPosition(luajava.new(Vector2, 750, 400))
    emitterActor:setAutoFire(true)
    emitterActor:setRotation(180)

    world:addInteractType(BALL)
    ball = luajava.new(Ball)
    ball:setPosition(luajava.new(Vector2, 300, 420))
    world:add(ball)
end

function onCheck(world, dropsCount)
    local counter = world:dropsInRect(-100, 200, 100, 200)

    if counter > 10 then
        return true
    else
        return false
    end
end