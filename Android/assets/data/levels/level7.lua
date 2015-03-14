function onCreate(world)
    world:setWinHint('Direct the flow to another exit')
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
    ground:addVertex(0, 250)
    world:add(ground)

    local ground = luajava.new(Ground)
    ground:addVertex(152, 224)
    ground:addVertex(650, 450)
    ground:addVertex(152, 272)
    ground:addVertex(0, 344)
    world:add(ground)

    local emitterActor = luajava.new(Emitter)
    emitterActor:setHasControl(false)
    world:add(emitterActor)
    emitterActor:setPosition(luajava.new(Vector2, 750, 400))
    emitterActor:setAutoFire(true)
    emitterActor:setRotation(180)

    ball = luajava.new(Ball)
    ball:setPosition(luajava.new(Vector2, 300, 420))
    world:add(ball)
end

function onCheck(dropsPosArray, dropsCount)
    local counter = 0
    for i = 0, dropsCount - 1 do
        local x = Array:get(dropsPosArray, i * 2)
        local y = Array:get(dropsPosArray, i * 2 + 1)
        if x < 0 and y > 200 then
            counter = counter + 1
        end
    end

    if counter > 10 then
        return true
    else
        return false
    end
end