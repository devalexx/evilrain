function onCreate(world)
    world:setWinHint('Playground!')
    --world:setPressingAction(1);

    ground = luajava.new(Ground)
    ground:addVertex(800, 480)
    ground:addVertex(792, 480)
    ground:addVertex(792, 8)
    ground:addVertex(8, 8)
    ground:addVertex(8, 472)
    ground:addVertex(792, 472)
    ground:addVertex(792, 480)
    ground:addVertex(0, 480)
    ground:addVertex(0, 0)
    ground:addVertex(800, 0)
    world:add(ground)

    for i = 0, 75 do
        for j = 0, 39 do
            drop = luajava.new(Drop)
            drop:setPosition(luajava.new(Vector2, i * 10 + 20, j * 10 + 20))
            if i > 30 then
                drop:setColor(Color.RED)
            else
                drop:setColor(Color.BLUE)
            end
            world:add(drop)
        end
    end
end

function onCheck(mArray)
    return false
end