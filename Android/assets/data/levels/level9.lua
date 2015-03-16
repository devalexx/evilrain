function onCreate(world)
    world:setWinHint('Push the button')
    world:setPressingAction(3);

    local ground = luajava.new(Ground)
    ground:addVertex(352, 16)
    ground:addVertex(456, 32)
    ground:addVertex(576, 120)
    ground:addVertex(552, 264)
    ground:addVertex(440, 300)
    ground:addVertex(440, 288)
    ground:addVertex(528, 200)
    ground:addVertex(528, 128)
    ground:addVertex(432, 56)
    ground:addVertex(264, 72)
    ground:addVertex(216, 184)
    ground:addVertex(256, 272)
    ground:addVertex(240, 300)
    ground:addVertex(168, 208)
    ground:addVertex(176, 128)
    ground:addVertex(232, 16)
    world:add(ground)

    local cloud = luajava.new(Cloud)
    cloud:setPosition(luajava.new(Vector2, 600, 420))
    world:add(cloud)
    cloud:setMinMaxPosRectangle(0, 300, 800, 200)

    trigger = luajava.new(Trigger)
    trigger:setPosition(luajava.new(Vector2, 420, 420))
    world:add(trigger)
end

function onCheck()
    --trigger:getBody():
    return false
end
