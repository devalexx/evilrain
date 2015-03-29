dynActorArray = {}

function onCreate()
    world:setPressingAction(PICKING_DROPS);

    local ground = luajava.new(Ground)
    ground:addVertex(776, 24)
    ground:addVertex(768, 344)
    ground:addVertex(744, 344)
    ground:addVertex(728, 168)
    ground:addVertex(656, 96)
    ground:addVertex(560, 96)
    ground:addVertex(512, 248)
    ground:addVertex(480, 248)
    ground:addVertex(480, 24)
    world:add(ground)

    ground = luajava.new(Ground)
    ground:addVertex(224, 40)
    ground:addVertex(336, 72)
    ground:addVertex(384, 160)
    ground:addVertex(328, 272)
    ground:addVertex(352, 168)
    ground:addVertex(296, 88)
    ground:addVertex(144, 88)
    ground:addVertex(112, 176)
    ground:addVertex(144, 264)
    ground:addVertex(80, 176)
    ground:addVertex(96, 64)
    world:add(ground)

    counter = 0
    for i = 15, 25 do
        for j = 9, 18 do
            local drop = luajava.new(Drop)
            drop:setPosition(luajava.new(Vector2, i * 10 + 20, j * 10 + 20))
            if i < 20 then
                drop:setColor(Color.GREEN)
            end
            counter = counter + 1
            world:add(drop)
        end
    end
end

function onCheck()
    if world:getDropsCount() ~= counter then
        return false
    end

    if world:dropsInRect(500, 50, 250, 200) ~= counter then
        return false
    end

    return true
end